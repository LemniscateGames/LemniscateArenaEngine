package lemniscate.data.fighters;

import lemniscate.data.Statuses;
import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.TargetType;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.SkillParams;
import lemniscate.engine.data.StatusData;

public class Vruh extends FighterData {
    public Vruh() {
        super(
                "vruh",
                "Vruh",
                3,
                ElementalType.STONE,
                FighterClass.ROGUE,
                4000, 500, 500, 500,
                "Vruh E. Momen LXIX is a seeker of Bruh Moments. Despite what he says, he has darker motivations unknown to anyone.",
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "Punch",
                    TargetType.ONE_ENEMY,
                    1
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("chance", .15);
            params.put("decrease", .5);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Epically attack a single enemy, with a %s chance to decrease Readiness by %s.",
                    user.percent("chance"), user.percent("decrease")
            );
        }

        @Override public void use(Fighter user) {
            user.dealDamage().onHit(target -> user.ifChance(() -> {
                target.decreaseReadiness(user.getDouble("decrease"));
            }));
        }
    }

    // ================================================================
    // -------- S2
    public static class SkillTwo extends SkillData {
        public SkillTwo() {
            super(
                    "Moment Search",
                    TargetType.ALL_FIGHTERS,
                    1,
                    4
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("buffCount", 2);
            params.put("debuffCount", 1);
            params.put("durationLower", 2);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Grant a random buff to a random ally %s. Inflict a random debuff on a random ally %s. Lower the cooldown of %s by %d.",
                    user.adverbal("buffCount"),
                    user.adverbal("debuffCount"),
                    user.getSkillName(2),
                    user.getInt("durationLower")
            );
        }

        private static final StatusData[] RANDOM_BUFFS = {
                Statuses.INCREASED_ATTACK, Statuses.INCREASED_DEFENSE, Statuses.INCREASED_SPEED
        };
        private static final StatusData[] RANDOM_DEBUFFS = {
                Statuses.DECREASED_ATTACK, Statuses.DECREASED_DEFENSE, Statuses.DECREASED_SPEED
        };

        @Override public void use(Fighter user) {
            user.repeat("buffCount", () -> {
                user.inflictStatus(user.randomAlly(), user.randomChoice(RANDOM_BUFFS), 2);
            });
            user.repeat("debuffCount", () -> {
                user.inflictStatus(user.randomEnemy(), user.randomChoice(RANDOM_DEBUFFS), 2);
            });

            user.lowerCooldown(2, "durationLower");
        }
    }

    // ================================================================
    // -------- S3
    public static class SkillThree extends SkillData {
        public SkillThree() {
            super(
                    "Ultimate Bruh Moment",
                    TargetType.ALL_ENEMIES,
                    1.11,
                    7,
                    2,
                    true
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("stunDur", 1);
            params.put("confuseDur", 2);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Achieve the ultimate Bruh Moment against all enemies, dealing high damage, stunning for %s and confusing for %s before defeating this fighter. This moves' cooldown begins at the start of combat.",
                    user.turnCount("stunDur"),
                    user.turnCount("confuseDur")
            );
        }

        @Override public String leDescription(Fighter user) {
            return "Ignores Resistance.";
        }

        @Override public void use(Fighter user) {
            user.forEachTarget(target -> {
                user.dealDamage(target).onHit(targetHit -> {
                    user.inflictStatus(target, Statuses.STUN, "stunDur", user.isLeBoosted());
                    user.inflictStatus(target, Statuses.CONFUSION, "confuseDur", user.isLeBoosted());
                });
            });

            user.die();
        }
    }
}
