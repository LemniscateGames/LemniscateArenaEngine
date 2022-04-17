package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Status;

/** An instance of a status being removed from a fighter. **/
public class ResistResult extends BattleResult {
    /** The target that resisted this effect. **/
    public final Fighter target;
    /** Whatever got resisted. Could be a status, readiness change, etc.
     * This should be a string or that object. **/
    public final Object effectResisted;

    public ResistResult(Fighter target, Object effectResisted) {
        this.target = target;
        this.effectResisted = effectResisted;
    }

    @Override
    public String toString(){
        return "Resisted "+effectResisted;
    }
}
