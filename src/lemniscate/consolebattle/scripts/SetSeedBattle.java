package lemniscate.consolebattle.scripts;

import lemniscate.consolebattle.ConsoleBattle;
import lemniscate.consolebattle.SeededBattleGenerator;
import lemniscate.data.ai.RandomChoiceAI;
import lemniscate.engine.battle.Battle;

public class SetSeedBattle {
    public static void main(String[] args) {
        long seed = -3108383802925573788L;

        Battle battle = SeededBattleGenerator.generateBattle(seed);
        ConsoleBattle.displayBattle(battle);
        ConsoleBattle.playBattleInConsole(battle, new RandomChoiceAI(seed));
    }
}
