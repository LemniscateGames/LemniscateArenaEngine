package lemniscate.data;

import lemniscate.data.statuses.*;
import lemniscate.engine.data.StatusData;

/** Utility class that holds a bunch of StatusData static instances that are used across code
 * (except for StatModifier which contains a bunch of stat permutations) **/
public class Statuses {
    // BUFFS
    public static final StatusData
            BARRIER = new Barrier(),
            REGENERATION = new Regeneration(),

    // DEBUFFS
            ASLEEP = new Asleep(),
            BURN = new Burn(),
            DAMAGE_BLESSING = new DamageBlessing(),
            IMMUNITY = new Immunity(),
            SAFEGUARD = new Safeguard(),
            STUN = new Stun(),
            UNHEALABLE = new Unhealable(),

    // STAT MODIFIERS
            INCREASED_ATTACK = new StatModifier(StatModifier.Stat.ATTACK, StatModifier.Magnitude.INCREASED),
            GREATLY_INCREASED_ATTACK = new StatModifier(StatModifier.Stat.ATTACK, StatModifier.Magnitude.GREATLY_INCREASED),
            DECREASED_ATTACK = new StatModifier(StatModifier.Stat.ATTACK, StatModifier.Magnitude.DECREASED),
            GREATLY_DECREASED_ATTACK = new StatModifier(StatModifier.Stat.ATTACK, StatModifier.Magnitude.GREATLY_DECREASED),

            INCREASED_DEFENSE = new StatModifier(StatModifier.Stat.DEFENSE, StatModifier.Magnitude.INCREASED),
            GREATLY_INCREASED_DEFENSE = new StatModifier(StatModifier.Stat.DEFENSE, StatModifier.Magnitude.GREATLY_INCREASED),
            DECREASED_DEFENSE = new StatModifier(StatModifier.Stat.DEFENSE, StatModifier.Magnitude.DECREASED),
            GREATLY_DECREASED_DEFENSE = new StatModifier(StatModifier.Stat.DEFENSE, StatModifier.Magnitude.GREATLY_DECREASED),

            INCREASED_SPEED = new StatModifier(StatModifier.Stat.SPEED, StatModifier.Magnitude.INCREASED),
            GREATLY_INCREASED_SPEED = new StatModifier(StatModifier.Stat.SPEED, StatModifier.Magnitude.GREATLY_INCREASED),
            DECREASED_SPEED = new StatModifier(StatModifier.Stat.SPEED, StatModifier.Magnitude.DECREASED),
            GREATLY_DECREASED_SPEED = new StatModifier(StatModifier.Stat.SPEED, StatModifier.Magnitude.GREATLY_DECREASED);
}
