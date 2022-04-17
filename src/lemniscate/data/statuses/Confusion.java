package lemniscate.data.statuses;

import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Skill;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.actions.ActAction;
import lemniscate.engine.battle.actions.TurnAction;
import lemniscate.engine.data.StatusData;

public class Confusion extends StatusData {
    public Confusion() {
        super(
                "Confused",
                StatusType.NEGATIVE,
                "Target is randomized when using skills."
        );

        addAction(ActAction.class, actAction -> {
            // Randomize the target selected when confused.
            Fighter actor = actAction.fighter;
            Skill skill = actAction.request.getSkill();

            Fighter randomTarget = actor.randomChoice(skill.getPossibleTargets(actor));
            actAction.request.setTarget(randomTarget);
        });

        addEffect(Trigger.TURN_END, this::tickDuration);
    }
}
