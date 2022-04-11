package lemniscate.data.statuses;

import lemniscate.engine.battle.Trigger;
import lemniscate.engine.data.StatusData;
import lemniscate.engine.StatusType;

public class Burn extends StatusData {
    public Burn() {
        super(
                "Burn",
                StatusType.NEGATIVE,
                "Take damage proportional to inflicter's ATK each turn."
        );

        addEffect(Trigger.TURN_START, (fighter) -> {
            fighter.takeDamage(
                    (int)(fighter.getStatus().inflicter.getAtk() * 0.3)
            );

            tickDuration(fighter);
        });
    }
}
