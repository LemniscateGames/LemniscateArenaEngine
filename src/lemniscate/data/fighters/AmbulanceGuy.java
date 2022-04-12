package lemniscate.data.fighters;

import lemniscate.data.Statuses;
import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.TargetType;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.results.DamageResult;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.SkillParams;

public class AmbulanceGuy extends FighterData {
    public AmbulanceGuy() {
        super(
                "ambulanceguy",
                "Ambulance Guy",
                4,
                ElementalType.WATER,
                FighterClass.RANGER,
                4275, 420, 550, 560,
                "",
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        // -- S1
        params.put("dmgHpLose", 0.125);
        params.put("dur", 2);
        params.put("hpStrength", 0.15);
        // -- S2
        params.put("dbDur", 3);
        params.put("hpThreshold", 0.5);
        params.put("buffDur", 2);
        // -- S3
        params.put("dur3", 1);
    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "Ambulance Ram",
                    TargetType.ONE_ENEMY,
                    1.25,
                    0,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return "Ram the opponent with an ambulance, losing HP proportional to damage dealt.";
        }

        @Override
        public String leDescription(Fighter user) {
            return String.format(
                    "After attacking, grant regeneration and a barrier for %s to the ally with the lowest HP. Barrier strength is proportional to max HP.",
                    user.turnCount("dur")
            );
        }

        @Override public void use(Fighter user) {
            DamageResult result = user.dealDamage();
            user.loseHP(user.proportion(result.damage, "dmgHpLose"));

            user.ifLeBoosted(() -> {
                user.allies().stream().max(Fighter.hpSort).ifPresent(ally -> {
                    user.inflictStatus(ally, Statuses.REGENERATION, user.getInt("dur"));
                    user.inflictStatus(ally, Statuses.BARRIER, "dur",
                            user.proportion(user.getMaxHp(), "hpStrength"));
                });
            });
        }
    }

    // ================================================================
    // -------- S2
    public static class SkillTwo extends SkillData {
        public SkillTwo() {
            super(
                    "Healing Gun",
                    TargetType.ONE_ALLY,
                    0.15,
                    4
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Shoot an ally with the Healing Gun, dealing damage proportional to their max HP and granting Damage Blessing for %s. If resulting HP is below %s, increase Attack and safeguard for %s.",
                    user.turnCount("dbDur"), user.percent("hpThreshold"), user.percent("buffDur")
            );
        }

        @Override public void use(Fighter user) {
            user.dealDamage(proportion(user.getTarget().getMaxHp(), power)).onHit(ally -> {
                user.inflictStatus(ally, Statuses.DAMAGE_BLESSING, "dbDur");
                if (ally.hpPercent() < user.getDouble("hpThreshold")){
                    user.inflictStatus(ally, Statuses.INCREASED_ATTACK, "buffDur");
                    user.inflictStatus(ally, Statuses.SAFEGUARD, "buffDur");
                }
            });
        }
    }

    // ================================================================
    // -------- S3
    public static class SkillThree extends SkillData {
        public SkillThree() {
            super(
                    "Power of the Gods",
                    TargetType.ONE_ENEMY,
                    0,
                    6
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Harnessing the power of the gods above, grant all allies immunity and safeguard for %s.",
                    user.turnCount("dur3")
            );
        }

        @Override public void use(Fighter user) {
            user.forEachAlly(ally -> {
                user.inflictStatus(ally, Statuses.IMMUNITY, "dur3");
                user.inflictStatus(ally, Statuses.SAFEGUARD, "dur3");
            });
        }
    }
}
