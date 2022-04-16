package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.data.SkillData;

/** The result of an action during battle that can be chained into other effects. */
public class ReadinessChangeResult extends BattleResult {
    /** The fighter whose readinesss was changed. **/
    public final Fighter fighter;
    /** The amount that readiness was changed by. **/
    public final double change;

    public ReadinessChangeResult(Fighter fighter, double change) {
        this.fighter = fighter;
        this.change = change;
    }

    @Override
    public String toString() {
        return String.format("%s %s's readiness by %s",
                change>0 ? "Raised" : "Lowered",
                fighter,
                SkillData.percent(Math.abs(change)));
    }
}
