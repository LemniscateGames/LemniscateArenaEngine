package lemniscate.engine.battle;

/** A request to be sent to a Battle's subitTurn() method to request a turn.
 * These four fields make up the data needed for the decision of any skill in the game.
 * (Three if you don't count the actor, that can be retreived from the battle as nextActor but it is included
 * in the request for safety measures) **/
public class TurnRequest {
    /** The actor that is taking this turn. **/
    public final Fighter actor;
    /** The skill this fighter is using. **/
    private Skill skill;
    /** The fighter this actor is using its skill on. **/
    private Fighter target;
    /** Whether this skill is being LE boosted or not. **/
    private boolean leBoosted;

    public TurnRequest(Fighter actor, Skill skill, Fighter target, boolean leBoosted) {
        this.actor = actor;
        this.skill = skill;
        this.target = target;
        this.leBoosted = leBoosted;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Fighter getTarget() {
        return target;
    }

    public void setTarget(Fighter target) {
        this.target = target;
    }

    public boolean isLeBoosted() {
        return leBoosted;
    }

    public void setLeBoosted(boolean leBoosted) {
        this.leBoosted = leBoosted;
    }

    @Override
    public String toString() {
        return String.format("%s is using %s on %s (le-boost: %s)", actor, skill, target, leBoosted);
    }
}
