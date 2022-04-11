package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.actions.TurnAction;
import lemniscate.engine.data.StatusData;

public class Asleep extends StatusData {
    public Asleep() {
        super(
                "Asleep",
                StatusType.NEGATIVE,
                "Cannot act. Awoken when attacked."
        );

        addAction(TurnAction.class, turnAction -> {
            // Fighter is asleep; cannot act.
            turnAction.setActive(false);
            turnAction.fighter.getBattle().addMessage(String.format("%s is asleep...", turnAction.fighter));
            tickDuration(turnAction.fighter);
            return turnAction;
        });

        addEffect(Trigger.POST_ATTACKED, (fighter) -> {
            // Remove this asleep status when attacked.
            fighter.getStatus().remove();
        });
    }
}
