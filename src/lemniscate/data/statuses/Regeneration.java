package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.data.StatusData;

public class Regeneration extends StatusData {
    public Regeneration() {
        super(
                "Regeneration",
                StatusType.POSITIVE,
                "Recover HP proportional to inflicter's max HP each turn."
        );

        addEffect(Trigger.TURN_START, (fighter) -> {
            fighter.heal((int)(fighter.getStatus().inflicter.getMaxHp() * 0.15));
        });

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}
