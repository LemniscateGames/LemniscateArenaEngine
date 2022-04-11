package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.actions.TurnAction;
import lemniscate.engine.data.StatusData;

public class Stun extends StatusData {
    public Stun() {
        super(
                "Stunned",
                StatusType.NEGATIVE,
                "Cannot act."
        );

        addAction(TurnAction.class, turnAction -> {
            // Fighter is stunned; cannot act.
            turnAction.setActive(false);
            turnAction.fighter.getBattle().addMessage(String.format("%s is stunned!", turnAction.fighter));
            tickDuration(turnAction.fighter);
            return turnAction;
        });
    }
}
