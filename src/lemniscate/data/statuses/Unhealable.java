package lemniscate.data.statuses;

import lemniscate.engine.battle.Trigger;
import lemniscate.engine.data.StatusData;
import lemniscate.engine.StatusType;

public class Unhealable extends StatusData {
    public Unhealable() {
        super(
                "Unhealable",
                StatusType.NEGATIVE,
                "Cannot be healed."
        );

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}
