package lemniscate.engine.battle;

import java.util.ArrayList;
import java.util.List;

/** A turn taken in a battle.
 * Contains all information required to reproduce the outcome of a battle at a given turn. **/
public class BattleTurn {
    private Fighter actor;
    private Skill skill;
    private Fighter target;
    private boolean leBoosted;
    /** Whether or not this fighter used a skill this turn, or lost their skill due to stun, asleep, etc. **/
    private boolean acted;
    /** List of events that happened during this turn. **/
    private final List<TurnEvent> events;

    public BattleTurn() {
        events = new ArrayList<>();
        acted = false;
    }
    public BattleTurn(Fighter actor){
        this();
        this.actor = actor;
    }

    public void setValues(Fighter actor, Skill skill, Fighter target, boolean leBoosted, boolean acted){
        this.actor = actor;
        this.skill = skill;
        this.target = target;
        this.leBoosted = leBoosted;
        this.acted = acted;
    }
    public void setValues(TurnRequest request, boolean acted){
        setValues(request.actor, request.skill, request.target, request.leBoosted, acted);
    }
    public void setValues(TurnRequest request){
        setValues(request.actor, request.skill, request.target, request.leBoosted, true);
    }

    public void addEvent(TurnEvent event){
        events.add(event);
    }

    public List<TurnEvent> getEvents() {
        return events;
    }

    // ACCESSORS
    public Fighter getActor() {
        return actor;
    }

    public void setActor(Fighter actor) {
        this.actor = actor;
    }

    public Fighter getTarget() {
        return target;
    }

    public void setTarget(Fighter target) {
        this.target = target;
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

    public boolean isActed() {
        return acted;
    }

    public void setActed(boolean acted) {
        this.acted = acted;
    }
}
