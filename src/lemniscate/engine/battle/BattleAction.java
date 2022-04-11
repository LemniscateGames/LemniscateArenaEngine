package lemniscate.engine.battle;

/** An action during battle that can be intercepted by "middleware" on fighter's statuses
 * to change this object's parameters.
 * For example, an attempt to target a fighter while another fighter has Taunt
 * may change the AttackAction's target to that fighter.
 */
public abstract class BattleAction {
    /** The fighter initiating this action, whatever it is. **/
    public final Fighter fighter;
    /** Whether this action has been stopped yet or not. Used in most actions that require it to be stopped,
     * so it's just used here instead of separately in every extension of this class. **/
    private boolean active;

    public BattleAction(Fighter fighter) {
        this.fighter = fighter;
        active = true;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
