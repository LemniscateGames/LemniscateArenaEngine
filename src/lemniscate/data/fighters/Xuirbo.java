package lemniscate.data.fighters;

import lemniscate.data.Statuses;
import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.TargetType;
import lemniscate.engine.battle.results.DamageResult;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.SkillParams;

import java.util.concurrent.atomic.AtomicInteger;

public class Xuirbo extends FighterData {
    public Xuirbo() {
        super(
                "xuirbo",
                "Xuirbo",
                4,
                ElementalType.THUNDER,
                FighterClass.ROGUE,
                3280, 780, 425, 650,
                "A businessman in the form of a small spherical knight.",
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        // -- S1
        params.put("decrease", 0.25);
        // -- S2
        params.put("hpLoseAmt", 0.075);
        params.put("burnDur", 1);
        params.put("buffDur", 2);
        params.put("leBuffTurns", 1);
        // -- S3
        params.put("spdDmg", 0.4);
        params.put("hpHpLoseAmt", 0.05);
        params.put("hpDmgLoseAmt", 0.10);
        params.put("stunChance", 0.6);
        params.put("stunDur", 1);
    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "Market Influence",
                    TargetType.ONE_ENEMY,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Deal damage to a single enemy, decreasing Readiness by %s if the target is stunned or asleep.",
                    user.percent("decrease")
            );
        }

        @Override public void use(Fighter user) {
            user.dealDamage().onHit((target) -> {
                if (target.hasStatus(Statuses.ASLEEP, Statuses.STUN)) {
                    target.decreaseReadiness(user.getDouble("decrease"));
                }
            });
        }
    }

    // ================================================================
    // -------- S2
    public static class SkillTwo extends SkillData {
        public SkillTwo() {
            super(
                    "Cigar",
                    TargetType.SELF,
                    1,
                    4,
                    1
            );
        }
        @Override public String description(Fighter user) {
            return String.format(
                    "Lose HP proportional to max HP and become burned for %s. Greatly increase Speed and Critical Damage for %s.",
                    user.turnCount("burnDur"), user.turnCount("buffDur")
            );
        }

        @Override public String leDescription(Fighter user) {
            return String.format(
                    "Extend all buffs and debuffs of this move by %s.",
                    user.turnCount("leBuffTurns")
            );
        }

        @Override public void use(Fighter user) {
            user.loseHP(proportion(user.getMaxHp(), user.getDouble("hpLoseAmt")));
            user.inflictStatus(Statuses.BURN, user.getInt("burnDur") + user.valIfLeBoosted("leBuffTurns"));

            user.gainStatus(Statuses.GREATLY_INCREASED_SPEED, user.getInt("buffDur") + user.valIfLeBoosted("leBuffTurns"));
            user.gainStatus(Statuses.GREATLY_INCREASED_ATTACK, user.getInt("buffDur") + user.valIfLeBoosted("leBuffTurns"));
        }
    }

    // ================================================================
    // -------- S3
    public static class SkillThree extends SkillData {
        public SkillThree() {
            super(
                    "Hyper Xuir Charge",
                    TargetType.ONE_ENEMY_PLUS_RANDOM,
                    0.9,
                    5
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Deal high damage to the target and another random enemy, with a %s chance to stun for %s. Lose HP proportional to max HP and damage dealt. Damage dealt increases proportional to this fighter's Speed.",
                    user.percent("stunChance"), user.turnCount("stunDur")
            );
        }

        @Override public void use(Fighter user) {
            AtomicInteger totalDamage = new AtomicInteger();
            user.forEachTarget(target -> {
                DamageResult result = user.dealDamage(target, user.skillDamage() + user.proportion(user.getSpd(), "spdDmg"));
                result.onHit(fighter -> {
                    if (user.chance("stunChance")){
                        fighter.inflictStatus(target, Statuses.STUN, user.getInt("stunDur"));
                    }
                });
                totalDamage.addAndGet(result.damage);
            });

            user.loseHP(user.proportion(user.getMaxHp(), "hpHpLoseAmt") + user.proportion(totalDamage.get(), "hpDmgLoseAmt"));
        }
    }
}
