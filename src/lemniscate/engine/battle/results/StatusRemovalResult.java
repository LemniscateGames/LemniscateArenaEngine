package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Status;
import lemniscate.engine.data.SkillData;

/** An instance of a status being removed from a fighter. **/
public class StatusRemovalResult extends BattleResult {
    /** The target of this status dispelling. **/
    private final Fighter target;
    /** The status dispelled. **/
    private final Status status;
    /** The way in which this status was dispelled (expired on time, dispelled, etc). **/
    private RemovalCause removalCause;
    public enum RemovalCause {
        EXPIRED,
        DISPELLED,
        SILENT
    }
    /** Whether this attempt to dispel a status was successful. **/
    private final boolean success;

    public StatusRemovalResult(Fighter target, Status status, RemovalCause removalCause, boolean success) {
        this.target = target;
        this.status = status;
        this.removalCause = removalCause;
        this.success = success;
    }

    // Effect extensions
    /** If this damage was dealt successfully, then the passed function is run. **/
    public void onHit(Runnable effect){
        if (success) effect.run();
    }

    public String toString() {
        if (success) {
            switch (removalCause) {
                case EXPIRED: return String.format("%s's %s expired", target, status);
                case DISPELLED: return String.format("Dispelled %s from %s", status, target);
            }
        }
        return null;
    }
}
