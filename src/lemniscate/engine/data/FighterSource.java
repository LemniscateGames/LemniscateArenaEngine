package lemniscate.engine.data;

public interface FighterSource {
    FighterData getData();
    int getRarity();
    int getLevel();
    int[] getSkillLevels();
    int getHp();
    int getAtk();
    int getDef();
    int getSpd();
    double getCritChance();
    double getCritDmg();
    double getEvasion();
    double getResistance();
    double getCounterPower();
    double getDualAttackPower();

}
