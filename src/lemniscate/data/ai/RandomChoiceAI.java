package lemniscate.data.ai;

import lemniscate.engine.Utils;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Skill;
import lemniscate.engine.battle.TurnRequest;
import lemniscate.engine.data.FighterAI;

import java.util.Random;

public class RandomChoiceAI implements FighterAI {
    private final Random rng;
    public final long seed;

    public RandomChoiceAI(long seed) {
        this.rng = new Random(seed);
        this.seed = seed;
    }
    public RandomChoiceAI(){
        this(new Random().nextLong());
    }

    @Override
    public TurnRequest decide(Fighter actor) {
        Skill skill = Utils.randomChoice(rng, actor.usableSkills());
        Fighter target = Utils.randomChoice(rng, skill.getPossibleTargets(actor));
        boolean leBoosted = actor.canLeBoost(skill) && rng.nextDouble() < 0.4;

        return new TurnRequest(actor, skill, target, leBoosted);
    }
}
