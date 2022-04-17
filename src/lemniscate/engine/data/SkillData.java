package lemniscate.engine.data;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.TargetType;

import java.util.List;
import java.util.function.Consumer;

/** Data for a fighter's skill. **/
public abstract class SkillData {
    /** Display name. **/
    public final String name;
    /** The type of targets that this skill targets. **/
    public final TargetType targetType;
    /** The power of this skill. Multiplied by user's ATK to get base damage. **/
    public final double power;
    /** Turn cooldown of this skill. **/
    public final int cooldown;
    /** Cost in LE to LE-boost this move. **/
    public final int leBoostCost;
    /** If set to true, this move will start with full cooldown instead of 0 when the battle begins. **/
    public final boolean beginWithCooldown;

    // Constructors
    public SkillData(String name, TargetType targetType, double power, int cooldown, int leBoostCost, boolean beginWithCooldown) {
        this.name = name;
        this.targetType = targetType;
        this.power = power;
        this.cooldown = cooldown;
        this.leBoostCost = leBoostCost;
        this.beginWithCooldown = beginWithCooldown;
    }
    public SkillData(String name, TargetType targetType, double power, int cooldown, int leBoostCost){
        this(name, targetType, power, cooldown, leBoostCost, false);
    }
    public SkillData(String name, TargetType targetType, double power, int cooldown) {
        this(name, targetType, power, cooldown, 0);
    }
    public SkillData(String name, TargetType targetType, double power) {
        this(name, targetType, power, 0);
    }

    // Abstract/Default Methods
    /** Description shown for this skill based on current skill state (usually a string format return). **/
    public abstract String description(Fighter user);
    /** Description for the LE boost of this skill. **/
    public String leDescription(Fighter user){ return ""; }
    /** Initialize all required parameters to the passed SkillParams object
     * for this skill to reference on the Fighter this SkillParams object is instantiated in later. **/
    public abstract void addParams(SkillParams params, int level);

    /** Effect to run when a fighter uses this skill. **/
    public abstract void use(Fighter user);

    // ======== Static utility
    // Skill effects
    public static void forEachFighter(List<Fighter> fighters, Consumer<Fighter> effect){
        for (Fighter fighter : fighters){
            effect.accept(fighter);
        }
    }

    public static void repeat(int amount, Runnable effect){
        for (int i=0; i<amount; i++){
            effect.run();
        }
    }

    // Damage
    /** Returns a percentage of the damage dealt with this move's power scaled by the passed portion.
     * Used primarily for increasing damage/another value proportionally by a stat. **/
    public static int proportion(int stat, double portion){
        return (int)(stat * portion);
    }
    /** Returns a percentage of the damage dealt with the passed power scaled by the passed portion.
     * Used primarily for increasing damage/another value proportionally by a stat. **/
    public static int proportion(double power, double stat, double portion){
        return (int)(power * stat * portion);
    }

    // Text used in descriptions
    public static String turnCount(int count){
            return count + (count == 1 ? " turn" : " turns");
    }

    public static String percent(double percent){
        return (int)(percent*100) + "%";
    }

    public static String verbal(int num){
        switch(num){
            case 1: return "one";
            case 2: return "two";
            case 3: return "three";
            case 4: return "four";
            case 5: return "five";
            default: return "zero";
        }
    }

    /** i dont care that adverbal probably is'nt a word **/
    public static String adverbal(int num){
        switch(num){
            case 1: return "once";
            case 2: return "twice";
            case 3: return "thrice";
            case 4: return "four times";
            case 5: return "five times";
            default: return "zero times";
        }
    }

    public static String buffCount(int count){
        return verbal(count) + (count == 1 ? " buff" : " buffs");
    }
    public static String debuffCount(int count){
        return verbal(count) + (count == 1 ? " debuff" : " debuffs");
    }
}
