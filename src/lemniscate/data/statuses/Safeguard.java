package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.results.DamageResult;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.StatusData;


public class Safeguard extends StatusData {
    public Safeguard() {
        super(
                "Safeguard",
                StatusType.POSITIVE,
                "If killed, revive at 25% HP."
        );

        addEffect(Trigger.ON_DEFEAT, fighter -> {
            fighter.removeAllStatuses();
            fighter.revive(.25);
        });
    }
}
