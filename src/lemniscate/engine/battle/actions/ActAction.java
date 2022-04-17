package lemniscate.engine.battle.actions;

import lemniscate.engine.battle.BattleAction;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.TurnRequest;

/** Runs when an actor submits their turn, before they act, which may modify their TurnRequest. **/
public class ActAction extends BattleAction {
    public final TurnRequest request;

    public ActAction(Fighter fighter, TurnRequest request) {
        super(fighter);
        this.request = request;
    }
}
