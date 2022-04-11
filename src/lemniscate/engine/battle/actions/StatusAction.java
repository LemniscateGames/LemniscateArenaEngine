package lemniscate.engine.battle.actions;

import lemniscate.engine.battle.BattleAction;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Status;

/** An attempt to inflict a status effect on another fighter.
 * Broadcasted on the target when the attacker attempts to deal damage to it. **/
public class StatusAction extends BattleAction {
    /** The inflicter of the status on the target. **/
    private Fighter inflicter;
    /** The status effect to inflict. **/
    private Status status;

    public StatusAction(Fighter target, Fighter attacker, Status status) {
        super(target);
        this.inflicter = attacker;
        this.status = status;
    }

    public Fighter getInflicter() {
        return inflicter;
    }

    public void setInflicter(Fighter inflicter) {
        this.inflicter = inflicter;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
