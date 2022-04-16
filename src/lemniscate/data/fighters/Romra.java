package lemniscate.data.fighters;

import lemniscate.data.Statuses;
import lemniscate.engine.ElementalType;
import lemniscate.engine.FighterClass;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.TargetType;
import lemniscate.engine.data.FighterData;
import lemniscate.engine.data.SkillData;
import lemniscate.engine.StatusType;
import lemniscate.engine.data.SkillParams;

import java.util.Optional;

public class Romra extends FighterData {
    public Romra() {
        super(
                "romra",
                "Romra",
                3,
                ElementalType.WIND,
                FighterClass.KNIGHT,
                4700, 405, 730, 325,
                "A newly certified knight in the Territory of Static Army who wishes to one day join the Elemental Offense Squad.",
                new BladeArm(), new HerosStrike(), new ShieldOfStatic()
        );
    }

    // ================================================================
    // -------- S1
    public static class BladeArm extends SkillData {
        public BladeArm() {
            super(
                    "Blade Arm",
                    TargetType.ONE_ENEMY,
                    0.65
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("chance", 0.35);
            params.put("dur", 2);
            params.put("defDmgBoost", 0.35);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Slash a single enemy with an arm blade, with a %s chance to increase Defense for %s. Damage increases proportional to this fighter's Defense.",
                    user.percent("chance"), user.turnCount("dur")
            );
        }

        @Override
        public void use(Fighter user) {
            user.dealDamage(user.skillDamage() + proportion(user.getDef(), user.getDouble("defDmgBoost")));

            if (user.chance("chance")){
                user.inflictStatus(user, Statuses.INCREASED_DEFENSE, user.getInt("dur"));
            }
        }
    }

    // ================================================================
    // -------- S2
    public static class HerosStrike extends SkillData {
        public HerosStrike() {
            super(
                    "Hero's Strike",
                    TargetType.ONE_ENEMY,
                    0.95,
                    4
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("dispelCount", 1);
            params.put("debuffDur", 2);
            params.put("hpDmgBoost", 0.09);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Deal damage to a single enemy, dispelling %s and decreasing Attack and inflicting unhealable for %s. Damage increases proportional to this fighter's current HP.",
                    user.buffCount("dispelCount"), user.turnCount("debuffDur")
            );
        }

        @Override
        public void use(Fighter user) {
            user.dealDamage(user.skillDamage() + proportion(user.getHp(), user.getDouble("hpDmgBoost")))
                    .onHit(target -> {
                        target.dispelStatuses(StatusType.POSITIVE, 1);
                        user.inflictStatus(Statuses.DECREASED_ATTACK, user.getInt("debuffDur"));
                        user.inflictStatus(Statuses.UNHEALABLE, user.getInt("debuffDur"));
                    });
        }
    }

    // ================================================================
    // -------- S3
    public static class ShieldOfStatic extends SkillData {
        public ShieldOfStatic() {
            super(
                    "Shield of Static",
                    TargetType.ALL_ALLIES,
                    0,
                    6,
                    2
            );
        }

        @Override
        public void addParams(SkillParams params, int level) {
            params.put("statDur", 1);
            params.put("strength", 1.25);
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Increase Defense and Attack and grant a barrier to all allies for %s.",
                    user.turnCount("statDur")
            );
        }

        @Override public String leDescription(Fighter user) {
            return "Greatly increase Attack and Defense.";
        }

        @Override public void use(Fighter user) {
            user.forEachAlly(ally -> {
                user.inflictStatus(ally, user.isLeBoosted() ? Statuses.GREATLY_INCREASED_DEFENSE : Statuses.INCREASED_DEFENSE,
                        user.getInt("statDur"));
                user.inflictStatus(ally, user.isLeBoosted() ? Statuses.GREATLY_INCREASED_ATTACK : Statuses.INCREASED_ATTACK,
                        user.getInt("statDur"));
                user.inflictStatus(ally, Statuses.BARRIER,
                        user.getInt("statDur"), user.proportion(user.getDef(), "strength"));
            });

//            Optional<Fighter> strongestAlly = user.allies(false).stream().max(Fighter.atkSort);
//            strongestAlly.ifPresent(ally -> {
//                Optional<Fighter> strongestEnemy = user.enemies().stream().max(Fighter.atkSort);
//                strongestEnemy.ifPresent(ally::dualAttack);
//            });
        }
    }
}
