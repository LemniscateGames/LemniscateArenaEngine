package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.results.DamageResult;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.StatusData;


public class DamageBlessing extends StatusData {
    public DamageBlessing() {
        super(
                "Damage Blessing",
                StatusType.POSITIVE,
                "Recover HP proportional to damage dealt when attacking."
        );

        addResult(DamageResult.class, damageResult -> {
            damageResult.attacker.heal(SkillData.proportion(damageResult.damage, 0.4));
        });

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}
