package lemniscate.engine.battle.actions;

import lemniscate.engine.battle.BattleAction;
import lemniscate.engine.battle.Fighter;

/** Runs before a turn is taken and a skill is decided. If active becomes false,
 * the turn is not taken. **/
public class TurnAction extends BattleAction {
    public TurnAction(Fighter fighter) {
        super(fighter);
    }
}
