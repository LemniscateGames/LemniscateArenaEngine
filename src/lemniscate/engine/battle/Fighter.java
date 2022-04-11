package lemniscate.engine.battle;

import lemniscate.engine.Utils;
import lemniscate.engine.battle.actions.AttackAction;
import lemniscate.engine.battle.actions.StatusAction;
import lemniscate.engine.battle.results.DamageResult;
import lemniscate.engine.battle.results.StatusResult;
import lemniscate.engine.battle.results.TurnEventMessage;
import lemniscate.engine.data.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** A fighter within a battle. Stored in server-side Battle objects. **/
public class Fighter {
    // ======== FIELDS
    // Immutable
    /** The data source this inherits its stats from. Can be a fighter entity or a local set of stats&info. **/
    public final FighterSource source;

    /** The battle that this fighter is initialized in. **/
    private Battle battle;

    /** The team that this fighter is on in the battle. **/
    private Team team;

    /** List of skill objects of this fighter, setup on initialization. **/
    public final Skill[] skills;

    /** The parameters for this fighter's skills that reflect values that may be based off the source's upgrades. **/
    public final SkillParams params;

    // Mutable
    /** The percentage that this fighter is ready.
     * Increases as time passes proportional to speed. **/
    private double readiness;

    /** The currently selected target of this fighter within the battle.
     * Set when choosing move. May be changed or swapped before using the move.
     * Not the same as the actual skill targets, this is just the target the player selected.
     * Ex. selecting an enemy with a move that targets all enemies will set this to the target
     * but the skill will actually hit all enemies.
     * For single target moves, either getTarget() or getTargets() will work for retreiving the target;
     * one will return the Fighter object while the other will return a list with just that fighter in it.
     */
    private Fighter target;

    /** The skill this fighter is currently using. **/
    private Skill skill;

    /** The targets selected by the cpu for the current skill to hit. **/
    private List<Fighter> targets;

    /** If this fighter is LE-boosting the skill they are currently using. **/
    private boolean leBoosted;

    /** Whether this fighter is currently dual attacking. **/
    private boolean dualAttacking;

    /** A list of all statuses active on this fighter. **/
    public ArrayList<Status> statuses;

    /** The status effect currently being evaluated on this fighter. **/
    private Status status;

    /** Return the name to be displayed in the current battle for this fighter.
     * Includes letters for duplicate fighters in the same battle. **/
    private String battleName;

    /** Current HP. **/
    private int hp;
    /** Calculated max HP. Updated on each stat change. **/
    private int maxHp;
    /** Calculated ATK. Updated on each stat change. **/
    private int atk;
    /** Calculated DEF. Updated on each stat change. **/
    private int def;
    /** Calculated SPD. Updated on each stat change. **/
    private int spd;

    // ======== CONSTRUCTOR
    public Fighter(FighterSource source) {
        this.source = source;

        // Construct params, getting all required params from fighter data according to level
        params = new SkillParams();
        getData().initializeParams(params, source.getSkillLevels());

        // Construct a Skill array with skills instantiated as containers for this fighter data's skill data,
        // storing the cooldown of the skill for that fighter and other info.
        skills = new Skill[source.getData().skills.length];
        for (int i=0; i<skills.length; i++){
            skills[i] = new Skill(source.getData().skills[i]);
        }

        // Initialize battle name as data name in case it is retrieved before adding to battle
        setBattleName(getData().name);

        // Initialize other battle stuff
        statuses = new ArrayList<>();
        updateStats();
        this.hp = maxHp;
    }

    // ======== GENERAL
    public boolean isAlive(){
        return hp > 0;
    }

    // ======== FIGHTERS
    /** Get all fighters on the same team as this fighter.
     * @param includeSelf whether this fighter should be included in the list.
     * @param onlyAlive whether to only return living fighters.
     * @return a list of all allies that match the test
     * **/
    public List<Fighter> allies(boolean includeSelf, boolean onlyAlive){
        ArrayList<Fighter> allies = new ArrayList<>(team.getFighters());
        if (!includeSelf) allies.remove(this);
        if (onlyAlive) allies.removeIf(fighter -> !fighter.isAlive());
        return allies;
    }
    public List<Fighter> allies(boolean includeSelf){
        return allies(includeSelf, true);
    }
    public List<Fighter> allies(){
        return allies(true, true);
    }

    public void forEachAlly(Consumer<Fighter> effect, boolean includeSelf){
        SkillData.forEachFighter(allies(includeSelf), effect);
    }
    public void forEachAlly(Consumer<Fighter> effect){
        forEachAlly(effect, true);
    }

    /** Returns a list of all fighters that are not on this fighter's team.
     *
     * @param onlyAlive whether to only return living enemies
     * @return a list of all enemies that match the test
     */
    public List<Fighter> enemies(boolean onlyAlive){
        if (battle.teams.length > 2){
            ArrayList<Fighter> fighters = new ArrayList<>();
            for (Team team : battle.teams){
                if (team != this.team){
                    for (Fighter fighter : team.getFighters()){
                        if (!onlyAlive || fighter.isAlive()) fighters.add(fighter);
                    }
                }
            }
            return fighters;
        } else {
            return battle.teams[ battle.teams[0] == team ? 1 : 0 ].getFighters();
        }
    }
    public List<Fighter> enemies(){
        return enemies(true);
    }
    public void forEachEnemy(Consumer<Fighter> effect){
        SkillData.forEachFighter(enemies(), effect);
    }

    public void forEachTarget(Consumer<Fighter> effect){
        SkillData.forEachFighter(targets, effect);
    }

    // ======== DAMAGE
    public DamageResult dealDamage(Fighter target, int damage){
        AttackAction action = new AttackAction(target, this, damage);
        target.broadcast(action);

        if (action.isActive()){
            action.getAttacker().takeDamage(action.getDamage());
            DamageResult result = new DamageResult(this, action.getAttacker(), action.getDamage(), true);
            battle.addEvent(result);
            return result;
        } else {
            return new DamageResult(this, action.getAttacker(), action.getDamage(), false);
        }
    }
    public DamageResult dealDamage(int damage){
        return dealDamage(target, damage);
    }
    public DamageResult dealDamage(Fighter target, double power){
        return dealDamage(target, (int)(power * atk * (dualAttacking ? source.getDualAttackPower() : 1)));
    }
    public DamageResult dealDamage(double power){
        return dealDamage(target, power);
    }
    public DamageResult dealDamage(Fighter target){
        return dealDamage(target, skillDamage());
    }
    public DamageResult dealDamage(){
        return dealDamage(target, skillDamage());
    }

    /** Receive a source of damage and reduce HP/barrier strengths accordingly.
     * Not tied to the attacker that may have dealt this damage (it may have not been a fighter),
     * but this is where this fighter's defense is calculated to reduce damage. **/
    public void takeDamage(int damage){
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            onDeath();
        }
    }
    /** Same as taking damage but HP will not go below 1. **/
    public void loseHP(int amount){
        if (hp > 0){
            hp -= amount;
            if (hp <= 0) hp = 1;
        }
    }

    /** The base damage this fighter will deal when using the current skill. **/
    public int skillDamage(){
        return (int)(skill.data.power * atk * (dualAttacking ? source.getDualAttackPower() : 1));
    }

    // ======== HEALING (aka what my mental state needs)
    public void heal(int amount){
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    // ======== DEATH
    /** Called when this fighter receives fatal damage. Usually dies unles they have the Revive buff or something. **/
    public void onDeath(){
        // Check the battle to see if it is over
        battle.check();

        // Clear all statuses
        for (Status status : new ArrayList<>(statuses)){
            removeStatus(status);
        }
    }

    // ======== STATUSES
    public StatusResult inflictStatus(Fighter target, StatusData statusData, int duration, int value){
        Status status = new Status(statusData, target, this, duration, value);

        StatusAction action = new StatusAction(target, this, status);
        target.broadcast(action);

        if (action.isActive()){
            target.addStatus(status);
            status.onInflict(target);
            StatusResult result = new StatusResult(this, target, status, true);
            battle.addEvent(result);
            return result;
        } else {
            return new StatusResult(this, target, status, false);
        }
    }
    public StatusResult inflictStatus(Fighter target, StatusData statusData, int duration){
        return inflictStatus(target, statusData, duration, 0);
    }
    public StatusResult inflictStatus(StatusData statusData, int duration){
        return inflictStatus(target, statusData, duration);
    }

    public StatusResult gainStatus(StatusData statusData, int duration){
        return inflictStatus(this, statusData, duration);
    }

    public void addStatus(Status status){
        statuses.add(status);
    }

    /** Remove a passed Status object from this fighter. **/
    public void removeStatus(Status status){
        statuses.remove(status);
        status.onRemove(this);
    }

    public void dispelStatuses(int statusType, int amount){
        // Find all statuses that can be dispelled by this
        ArrayList<Status> dispellableStatuses = new ArrayList<>();
        for (Status status : statuses) {
            if (status.data.type == statusType){
                dispellableStatuses.add(status);
            }
        }

        // While there are still statuses that can be dispelled, dispel one and stop if size is 0
        for (int i=0; i<amount; i++){
            if (dispellableStatuses.size() == 0) break;
            Status status = dispellableStatuses.get(battle.rng.nextInt(dispellableStatuses.size()));
            removeStatus(status);
            battle.addEvent(new TurnEventMessage(
                    String.format("Dispelled %s from %s",
                            status, this)
            ));
        }
    }

    public boolean hasStatus(StatusData statusData){
        for (Status status : statuses){
            if (status.data == statusData) return true;
        }
        return false;
    }
    public boolean hasStatus(StatusData... statusDatas){
        for (StatusData statusData : statusDatas){
            if (hasStatus(statusData)) return true;
        }
        return false;
    }

    public boolean hasStatus(String name){
        for (Status status : statuses){
            if (status.data.name.equals(name)) return true;
        }
        return false;
    }
    public boolean hasStatus(String... names){
        for (String name : names){
            if (hasStatus(name)) return true;
        }
        return false;
    }


    // ======== SKILLS
    /** Have this fighter use a skill. **/
    public void use(Skill skill, Fighter target, boolean leBoosted){
        if (skill.getCurrentCooldown() > 0) return;

        // Set LEBoosted to true if called as true
        if (leBoosted) this.leBoosted = true;

        // LE is stored as one int value = one tenth LE, but the skill LE costs are stored as actual LE cost,
        // so that's why it's multiplied by 10 here
        team.setLe(team.getLe() - skill.data.leBoostCost*10);

        // Set current cooldown to its internal from data
        skill.resetCooldown();

        // Set internal skill field to be referenced later by skillDamage(), etc
        this.skill = skill;

        // Set internal selected target to passed target
        this.target = target;
        // Define targets that will be targeted by this move. This modifies the targets field (and may use RNG).
        generateTargets();

        // Add a turn event message as a sort of header.
        // If another skill is called in the middle of this skill this will also denote that.
        // If possible targets is different from actual targets, state the target(s). otherwise, don't
        if (Utils.allItemsShared(getPossibleTargets(), targets)){
            battle.addMessage(String.format("%s uses %s",
                    this, skill));
        } else {
            if (dualAttacking) {
                battle.addMessage(String.format("%s dual-attacks %s", // with %s
                        this, targets));
            } else {
                battle.addMessage(String.format("%s uses %s on %s",
                        this, skill, targets));
            }
        }

        // Trigger the skill's effect
        skill.data.use(this);

        // reset leBoost to false after using skill
        this.leBoosted = false;
    }
    public void use(Skill skill, Fighter target){
        use(skill, target, false);
    }

    /** Use the skill with the given index on this fighter in the skill list. **/
    public void use(int index, Fighter target, boolean leBoosted){
        use(skills[index], target, leBoosted);
    }
    public void use(int index, Fighter target){
        use(skills[index], target);
    }

    public List<Skill> usableSkills(){
        return Arrays.stream(skills).filter(skill -> skill.isUsable(this)).collect(Collectors.toList());
    }

    // ======== TARGETS
    public List<Fighter> getPossibleTargets() {
        return skill.data.targetType.getPossibleTargets(this);
    }

    /** Generate targets to be targeted by the current skill.
     * (This may make RNG calls, such as if the target type is random!)
     * @return the list of targets generated.
     */
    public List<Fighter> generateTargets() {
        targets = skill.data.targetType.getTargets(this);
        return targets;
    }

    /** Launch an attack with first skill on a target,
     * usually as a chain from an attack from another fighter.
     */
    public void dualAttack(Fighter target){
        boolean tempDualAttacking = dualAttacking;
        dualAttacking = true;
        use(0, target);
        dualAttacking = tempDualAttacking;
    }

    /** Trigger a fighter to dual attack another fighter. **/
    public void triggerDualAttack(Fighter fighter, Fighter target){
        fighter.dualAttack(target);
    }
    public void triggerDualAttack(Fighter fighter){
        triggerDualAttack(fighter, target);
    }

    // ======== READINESS
    public double timeUntilReady(){
        return (1-readiness) / spd;
    }

    public void passTime(double time){
        if (isAlive()) this.readiness += time * spd;
    }

    private void changeReadiness(double amount){
        readiness += amount;
        if (readiness > 1) readiness = 1;
        if (readiness < 0) readiness = 0;
    }
    public void increaseReadiness(double amount){
        changeReadiness(amount);
    }
    public void decreaseReadiness(double amount){
        changeReadiness(-amount);
    }
    public void decreaseReadiness(String key){
        decreaseReadiness(getDouble(key));
    }

    // ======== LE
    public int valIfLeBoosted(int num){
        return leBoosted ? num : 0;
    }
    public double valIfLeBoosted(double num){
        return leBoosted ? num : 0;
    }
    public int valIfLeBoosted(String key){
        return leBoosted ? getInt(key) : 0;
    }

    public void ifLeBoosted(Runnable effect){
        if (leBoosted) effect.run();
    }
    /** Return true if this fighter's team can LE-boost the given move. **/
    public boolean canLeBoost(Skill skill){
        return team.canLeBoost(skill);
    }

    // ======== EVENT TRIGGERING

    /** Trigger an event that doesn't require passing in a BattleAction that may be "cancelled" or otherwise modified
     * (such as PRE_ACT, POST_ACT, etc)
     * @param trigger the trigger to invoke
     */
    public void invoke(Trigger trigger){
        for (Status status : new ArrayList<>(statuses)){
            this.status = status;
            status.invoke(this, trigger);
        }
    }

    // ======== ACTIONS/BROADCASTING
    /** Broadcast an action. Any statuses which react to the action's class may react and
     * modify the action's parameters.
     * @param action the BattleAction to be possibly modified, by each status on this fighter
     */
    public void broadcast(BattleAction action){
        // Apply the passed action to all matching statuses.
        // Creates a copy of status references to loop over,
        // because the actual status list may be modified while running the effects of the actions,
        // i.e. asleep being removed when damaged.
        for (Status status : new ArrayList<>(statuses)){
//            // Make sure the status hasn't been cleared by another status effect already while looping
//            if (statuses.contains(status)){
                // If still present, select it and apply the action
                this.status = status;
                status.apply(action);
//            }
        }
    }

    // ======== STATS
    /** SHould be called whenever this fighter's stats are updated.
     * Resets stats and recalculates all stat changes. **/
    public void updateStats(){
        // Initialize back to source values
        maxHp = source.getHp();
        atk = source.getAtk();
        def = source.getDef();
        spd = source.getSpd();

        // invoke stat update, triggering all stat modifiers and buffs to modify this fighter's stats,
        // such as increased attack, lowered defense, etc. they will modify the fields
        invoke(Trigger.STAT_UPDATE);
    }

    // ======== MISC
    /** Make an RNG call and if passed, run the effect. **/
    public void ifChance(double chance, Runnable effect){
        if (chance(chance)) {
            effect.run();
        }
    }

    // ======== COMPARATORS
    /** Compare fighters in order of HP. **/
    public static final Comparator<Fighter> hpSort = Comparator.comparingInt(a -> a.hp);
    /** Compare fighters in order of ATK. **/
    public static final Comparator<Fighter> atkSort = Comparator.comparingInt(a -> a.atk);

    /** Compare fighters in order of when they will next act. **/
    public static final Comparator<Fighter> actOrderSort = (a, b) -> {
        double at = a.timeUntilReady();
        double bt = b.timeUntilReady();
        if (at == bt){
            return 0;
        } else {
            return at > bt ? 1 : -1;
        }
    };

    // ======== PARAMETERS
    public int getInt(String key){
        if (!params.ints.containsKey(key)) try {
            throw new ParameterDNEException(this, key);
        } catch (ParameterDNEException e) {
            e.printStackTrace();
            return 0;
        }
        return params.ints.get(key);
    }
    public double getDouble(String key){
        if (!params.doubles.containsKey(key)) try {
            throw new ParameterDNEException(this, key);
        } catch (ParameterDNEException e) {
            e.printStackTrace();
            return 0;
        }
        return params.doubles.get(key);
    }

    public String turnCount(String key){
        return SkillData.turnCount(getInt(key));
    }

    public String percent(String key){
        return SkillData.percent(getDouble(key));
    }

    public String verbal(String key){
        return SkillData.verbal(getInt(key));
    }

    public String buffCount(String key){
        return SkillData.buffCount(getInt(key));
    }
    public String debuffCount(String key){
        return SkillData.debuffCount(getInt(key));
    }

    // ======== Accessors but not standard field accessors whatever those should be called
    public FighterData getData() {
        return source.getData();
    }

    /** Get all the targets targeted by the current skill being used. **/
    public List<Fighter> getTargets(){
        return targets;
    }

    public boolean chance(double p){
        return battle.chance(p);
    }
    public boolean chance(String key){
        return chance(getDouble(key));
    }
    public int proportion(double stat, String portionKey){
        return SkillData.proportion(stat, getDouble(portionKey));
    }

    // ======== ACCESSORS
    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Fighter getTarget() {
        return target;
    }

    public void setTarget(Fighter target) {
        this.target = target;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void addAtk(double atk) {
        this.atk += (int)atk;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void addDef(double def) {
        this.def += (int)def;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public void addSpd(double spd) {
        this.spd += (int)spd;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public boolean isLeBoosted() {
        return leBoosted;
    }

    public void setLeBoosted(boolean leBoosted) {
        this.leBoosted = leBoosted;
    }

    public double getReadiness() {
        return readiness;
    }

    public void setReadiness(double readiness) {
        this.readiness = readiness;
    }

    public String getBattleName() {
        return battleName;
    }

    public void setBattleName(String battleName) {
        this.battleName = battleName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // toString && others
    @Override
    public String toString() {
        return getBattleName();
    }
}
