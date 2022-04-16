package lemniscate.data.statuses;

import lemniscate.engine.battle.Status;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.data.StatusData;
import lemniscate.engine.StatusType;

public class Burn extends StatusData {
    public Burn() {
        super(
                "Burn",
                StatusType.NEGATIVE,
                "Lose HP proportional to inflicter's ATK each turn."
        );

        addEffect(Trigger.TURN_START, (fighter) -> {
            Status burn = fighter.getStatus();

            fighter.loseHP((int)(fighter.getStatus().inflicter.getAtk() * 0.3));

            tickDuration(fighter);
        });
    }
}
