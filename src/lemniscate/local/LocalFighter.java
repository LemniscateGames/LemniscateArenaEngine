package lemniscate.local;

import lemniscate.engine.Formulas;
import lemniscate.engine.data.FighterSource;
import lemniscate.engine.data.FighterData;

import java.util.Random;

/** Represents the database fighter entity, but it is local instead. **/
public class LocalFighter implements FighterSource {
    private final FighterData data;
    private int level;

    public LocalFighter(FighterData data, int level) {
        this.data = data;
        this.level = level;
    }
    public LocalFighter(FighterData data, int levelLow, int levelHigh, Random rng){
        this(data, levelLow + rng.nextInt((levelHigh-levelLow)));
    }

    public int statCurve(int stat){
        return Formulas.statCurve(stat, level);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int[] getSkillLevels() {
        return new int[]{0,0,0};
    }

    @Override
    public FighterData getData() {
        return data;
    }

    @Override
    public int getHp() {
        return statCurve(data.baseHp);
    }

    @Override
    public int getAtk() {
        return statCurve(data.baseAtk);
    }

    @Override
    public int getDef() {
        return statCurve(data.baseDef);
    }

    @Override
    public int getSpd() {
        return statCurve(data.baseSpd);
    }

    @Override
    public double getCritChance() {
        return 0.1;
    }

    @Override
    public double getCritDmg() {
        return 0.5;
    }

    @Override
    public double getEvasion() {
        return 0.0;
    }

    @Override
    public double getResistance() {
        return 0.0;
    }

    @Override
    public double getCounterPower() {
        return 0.75;
    }

    @Override
    public double getDualAttackPower() {
        return 0.75;
    }
}
