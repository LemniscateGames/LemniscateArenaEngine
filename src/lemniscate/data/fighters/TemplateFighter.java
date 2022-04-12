package lemniscate.data.fighters;

import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.TargetType;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.data.SkillParams;

public class TemplateFighter extends FighterData {
    public TemplateFighter() {
        super(
                "template",
                "Template",
                3,
                ElementalType.FLAME,
                FighterClass.WARRIOR,
                4000, 500, 500, 500,
                "",
                new SkillOne(), new SkillTwo(), new SkillThree()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        // -- S1

        // -- S2

        // -- S3

    }

    // ================================================================
    // -------- S1
    public static class SkillOne extends SkillData {
        public SkillOne() {
            super(
                    "",
                    TargetType.ONE_ENEMY,
                    1
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
