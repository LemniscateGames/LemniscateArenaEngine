package lemniscate.data.statuses;

import lemniscate.engine.battle.Status;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.actions.AttackAction;
import lemniscate.engine.battle.results.StatusRemovalResult;
import lemniscate.engine.data.StatusData;
import lemniscate.engine.StatusType;

/** Adds temporary "extra HP" to a fighter.
 * Remaining HP is stored as the value field in the Status object.
 * Damage negation is handled by the fighter's takeDamage function. **/
public class Barrier extends StatusData {
    public Barrier() {
        super(
                "Barrier",
                StatusType.POSITIVE,
                "Adds temporary \"extra HP\" to this fighter."
        );

        // When a fighter attempts to deal damage to another with a barrier,
        // decrease the damage dealt by the fighter by this barrier's skill;
        // if there is leftover damage, pass it on, otherwise leave damage as 0
        addAction(AttackAction.class, battleAction -> {
            Status barrier = battleAction.fighter.getStatus();
            int barrierDamage;

            // If damage is enough to clear the status, clear it and subtract damage.
            // Otherwise, put all remaining damage into the status and set damage to 0.
            if (barrier.getValue() <= battleAction.getDamage()) {
                barrierDamage = battleAction.getDamage() - barrier.getValue();
                battleAction.setDamage(barrierDamage);
                barrier.remove(StatusRemovalResult.RemovalCause.SILENT);
            } else {
                barrierDamage = barrier.getValue() - battleAction.getDamage();
                barrier.setValue(barrierDamage);
                battleAction.setDamage(0);
            }

            battleAction.fighter.addResult("barrierDamage", barrierDamage,
                    String.format("%s's barrier took %d damage", battleAction.fighter, barrierDamage));
        });

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}