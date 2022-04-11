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
                2500, 500, 500, 500,
                "A newly certified knight in the Territory of Static Army who wishes to one day join the Elemental Offense Squad.",
                new BladeArm(), new HerosStrike(), new ShieldOfStatic()
        );
    }

    // -------- PARAMS
    @Override public void initializeParams(SkillParams params, int[] levels) {
        // -- S1
        params.put("chance", 0.35);
        params.put("dur", 2);
        params.put("defDmgBoost", 0.4);
        // -- S2
        params.put("dispelCount", 1);
        params.put("attackDecreaseDur", 3);
        params.put("unhealableDur", 2);
        params.put("hpDmgBoost", 0.15);
        // -- S3
        params.put("statBuffDur", 1);
        params.put("barrierDur", 2);
        params.put("extendDur", 1);
    }

    // ================================================================
    // -------- S1
    public static class BladeArm extends SkillData {
        public BladeArm() {
            super(
                    "Blade Arm",
                    TargetType.ONE_ENEMY,
                    0.6
            );
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
                    0.8,
                    4
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Deal damage to a single enemy, dispelling %s, decreasing Attack for %s and inflicting unhealable for %s. Damage increases proportional to this fighter's current HP.",
                    user.buffCount("dispelCount"), user.turnCount("attackDecreaseDur"), user.turnCount("unhealableDur")
            );
        }

        @Override
        public void use(Fighter user) {
            user.dealDamage(user.skillDamage() + proportion(user.getHp(), user.getDouble("hpDmgBoost")))
                    .onHit(target -> {
                        target.dispelStatuses(StatusType.POSITIVE, 1);
                        user.inflictStatus(Statuses.DECREASED_ATTACK, user.getInt("attackDecreaseDur"));
                        user.inflictStatus(Statuses.UNHEALABLE, user.getInt("unhealableDur"));
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
                    5,
                    2
            );
        }

        @Override public String description(Fighter user) {
            return String.format(
                    "Greatly increase Defense and Attack of all allies for %s and grant a barrier for %s, before triggering a Dual Attack from the strongest ally on the strongest enemy.",
                    user.turnCount("statBuffDur"), user.turnCount("barrierDur")
            );
        }

        @Override public String leDescription(Fighter user) {
            return String.format("Extend buffs granted by %s.", user.turnCount("extendDur"));
        }

        @Override public void use(Fighter user) {
            user.forEachAlly(ally -> {
                user.inflictStatus(ally, Statuses.GREATLY_INCREASED_DEFENSE,
                        user.getInt("statBuffDur") + (user.isLeBoosted() ? user.getInt("extendDur") : 0));
                user.inflictStatus(ally, Statuses.GREATLY_INCREASED_ATTACK,
                        user.getInt("statBuffDur") + (user.isLeBoosted() ? user.getInt("extendDur") : 0));
                user.inflictStatus(ally, Statuses.BARRIER,
                        user.getInt("barrierDur") + (user.isLeBoosted() ? user.getInt("extendDur") : 0));
            });

            Optional<Fighter> strongestAlly = user.allies(false).stream().max(Fighter.atkSort);
            strongestAlly.ifPresent(ally -> {
                Optional<Fighter> strongestEnemy = user.enemies().stream().max(Fighter.atkSort);
                strongestEnemy.ifPresent(ally::dualAttack);
            });
        }
    }
}
