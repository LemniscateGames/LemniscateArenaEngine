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
                "AmbulanceGuy",
                4,
                ElementalType.WATER,
                FighterClass.RANGER,
                2500, 500, 500, 500,
                "",
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        // -- S1
        params.put("dmgHpLose", 0.25);
        params.put("dur", 2);
        params.put("hpStrength", 0.15);
        // -- S2

        // -- S3

    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "Ambulance Ram",
                    TargetType.ONE_ENEMY,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return "Ram the opponent with an ambulance, before losing HP proportional to damage dealt.";
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
                    user.inflictStatus(ally, Statuses.BARRIER, user.getInt("dur"),
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
                    "",
                    TargetType.ONE_ENEMY,
                    1,
                    4
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "",
                    user
            );
        }

        @Override public void use(Fighter user) {

        }
    }

    // ================================================================
    // -------- S3
    public static class SkillThree extends SkillData {
        public SkillThree() {
            super(
                    "",
                    TargetType.ONE_ENEMY,
                    0,
                    5,
                    1
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "",
                    user
            );
        }

        @Override public String leDescription(Fighter user) {
            return String.format(
                    "",
                    user
            );
        }

        @Override public void use(Fighter user) {

        }
    }
}
