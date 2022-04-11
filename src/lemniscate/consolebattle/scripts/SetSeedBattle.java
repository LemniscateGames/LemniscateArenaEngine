package lemniscate.consolebattle.scripts;

import lemniscate.consolebattle.ConsoleBattle;
import lemniscate.consolebattle.SeededBattleGenerator;
import lemniscate.data.ai.RandomChoiceAI;

public class SetSeedBattle {
    public static void main(String[] args) {
        long seed = -3108383802925573788L;

        ConsoleBattle.playBattleInConsole(
                SeededBattleGenerator.generateBattle(seed),
                new RandomChoiceAI(seed)
        );
    }
}
