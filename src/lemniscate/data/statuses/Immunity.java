package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.actions.StatusAction;
import lemniscate.engine.data.StatusData;

public class Immunity extends StatusData {
    public Immunity() {
        super(
                "Immunity",
                StatusType.POSITIVE,
                "Cannot gain any debuffs."
        );

        addAction(StatusAction.class, statusAction -> {
            if (statusAction.getStatus().data.type == StatusType.NEGATIVE) {
                statusAction.setActive(false);
            }
        });

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}
