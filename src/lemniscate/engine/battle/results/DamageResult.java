package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;

import java.util.function.Consumer;

/** An instance of a fighter taking damage.
 * Broadcast on the attacker, unlike AttackAction which is broadcast on the target. **/
public class DamageResult extends BattleResult {
    /** The attacker dealing the damage. **/
    public final Fighter attacker;
    /** The receiver of this damage. **/
    public final Fighter target;
    /** The amount of damage dealt. **/
    public final int damage;
    /** Whether this attempt to deal damage hit successfully. **/
    public final boolean hit;

    public DamageResult(Fighter attacker, Fighter target, int damage, boolean hit) {
        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
        this.hit = hit;
    }

    // Effect extensions
    /** If this damage was dealt successfully, then the passed function is run. **/
    public void onHit(Consumer<Fighter> effect){
        if (hit) effect.accept(target);
    }

    @Override
    public String toString() {
        if (hit) {
            return String.format("%s took %d damage", target, damage);
        } else {
            return null;
        }
    }
}
