package lemniscate.data.fighters.abstractFighters;

import lemniscate.data.Statuses;
import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.TargetType;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.SkillParams;

import java.util.Optional;

public abstract class CollectUnity extends FighterData {
    public CollectUnity(boolean isCollect) {
        super(
                isCollect ? "collect" : "unity",
                isCollect ? "Collect" : "Unity",
                3,
                ElementalType.FLAME,
                FighterClass.WARRIOR,
                2500, 500, 500, 500,
                String.format(
                        "A traveller of multiverses who finds %s pitted against strong fighters from across multiple universes.",
                        isCollect ? "himself" : "herself"
                ),
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        params.put("decrease", 0.1);
        params.put("buffChance", 0.5);
        params.put("buffDur", 2);
        params.put("lePwrMult", 1.4);
    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "Ordain",
                    TargetType.ONE_ENEMY,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Swing a blade at one enemy, decreasing Readiness by %s.",
                    user.percent("decrease")
            );
        }

        @Override public void use(Fighter user) {
            user.dealDamage().onHit(target -> target.decreaseReadiness(user.getDouble("decrease")));
        }
    }

    // ================================================================
    // -------- S2
    public static class SkillTwo extends SkillData {
        public SkillTwo() {
            super(
                    "Dual Slash",
                    TargetType.ONE_ENEMY,
                    1,
                    4
            );
        }

        @Override public String description(Fighter user) {
            return "Slash a single enemy, before triggering a Dual Attack from the strongest ally.";
        }

        @Override public void use(Fighter user) {
            user.dealDamage();
            Optional<Fighter> strongestAlly = user.allies(false).stream().max(Fighter.atkSort);
            strongestAlly.ifPresent(ally -> ally.dualAttack(user.getTarget()));
        }
    }

    // ================================================================
    // -------- S3
    public static class SkillThree extends SkillData {
        public SkillThree() {
            super(
                    "Unifying Blades",
                    TargetType.ALL_ENEMIES,
                    0.5,
                    5,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Attack all enemies with heated blades, before a %s chance each to increase Attack and Defense of all allies for %s.",
                    user.percent("buffChance"), user.turnCount("buffDur")
            );
        }

        @Override public String leDescription(Fighter user) {
            return "Increases damage dealt.";
        }

        @Override public void use(Fighter user) {
            user.forEachTarget(target -> user.dealDamage(target, user.isLeBoosted() ? power*user.getDouble("lePwrMult") : power));
            user.forEachAlly(ally -> {
                user.ifChance(user.getDouble("buffChance"), () -> user.inflictStatus(ally, Statuses.INCREASED_ATTACK, user.getInt("buffDur")));
                user.ifChance(user.getDouble("buffChance"), () -> user.inflictStatus(ally, Statuses.INCREASED_DEFENSE, user.getInt("buffDur")));
            });
        }
    }
}
