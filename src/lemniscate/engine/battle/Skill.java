package lemniscate.engine.battle;

import lemniscate.engine.data.SkillData;

import java.util.List;

/** A skill instantiated on a Fighter. Stores an internal cooldown. **/
public class Skill {
    /** The base data for this skill this is an instance of. **/
    public final SkillData data;
    /** Turns until usable again. **/
    private int currentCooldown;

    // Constructors
    public Skill(SkillData data) {
        this.data = data;
    }

    /** If this skill is usable by the given fighter. **/
    public boolean isUsable(Fighter user){
        return currentCooldown == 0;
    }

    /** Reset this skill's cooldown to the internal skill's base cooldown. **/
    public void resetCooldown() {
        this.currentCooldown = data.cooldown;
    }

    /** Get total LE cost in tenths as ints (ex. a 1 LE move will return 10 from this) **/
    public int getLeCost(){
        return data.leBoostCost * 10;
    }
    /** Whether or not LE boosting this move will have any effect and therefore if it should be shown at all. **/
    public boolean isLeBoostable(){
        return data.leBoostCost > 0;
    }

    // Accessors, kinda
    public String getName() {
        return data.name;
    }

    public List<Fighter> getPossibleTargets(Fighter fighter){
        return data.targetType.getPossibleTargets(fighter);
    }

    public List<Fighter> getTargets(Fighter fighter){
        return data.targetType.getTargets(fighter);
    }

    // Accessors
    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    // toString

    @Override
    public String toString() {
        return getName();
    }
}
