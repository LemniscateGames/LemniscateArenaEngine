package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Status;
import lemniscate.engine.data.SkillData;

/** An instance of a fighter taking damage. **/
public class StatusResult extends BattleResult {
    /** The attacker inflicting this status. **/
    private final Fighter inflicter;
    /** The target of this status infliction. **/
    private final Fighter target;
    /** The status inflicted. **/
    private final Status status;
    /** Whether this attempt to inflict a status was successful. **/
    private final boolean success;

    public StatusResult(Fighter inflicter, Fighter target, Status status, boolean success) {
        this.inflicter = inflicter;
        this.target = target;
        this.status = status;
        this.success = success;
    }

    // Effect extensions
    /** If this damage was dealt successfully, then the passed function is run. **/
    public void onHit(Runnable effect){
        if (success) effect.run();
    }

    public String toString() {
        if (success) {
            return String.format(
                    "Inflicted %s on %s for %s",
                    status, target, SkillData.turnCount(status.getDuration()));
        } else {
            return null;
        }
    }
}
