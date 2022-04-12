package lemniscate.engine.battle.actions;

import lemniscate.engine.battle.BattleAction;
import lemniscate.engine.battle.Fighter;

/** An attempt to deal damage to a fighter.
 * Broadcasted on the target when the attacker attempts to deal damage to it. **/
public class AttackAction extends BattleAction {
    /** The attacker. **/
    private Fighter attacker;
    /** The amount of damage to deal. **/
    private int damage;

    public AttackAction(Fighter target, Fighter attacker, int damage) {
        super(target);
        this.attacker = attacker;
        this.damage = damage;
    }

    public Fighter getAttacker() {
        return attacker;
    }

    public void setAttacker(Fighter attacker) {
        this.attacker = attacker;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Fighter getTarget(){
        return getFighter();
    }
}
