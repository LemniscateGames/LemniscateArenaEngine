package lemniscate.consolebattle.scripts;

import lemniscate.consolebattle.ConsoleBattle;
import lemniscate.consolebattle.SeededBattleGenerator;
import lemniscate.data.ai.RandomChoiceAI;

import java.util.Random;

public class RandomSeedBattle {
    public static void main(String[] args) {
        Random rng = new Random();
        long seed = rng.nextLong();

        ConsoleBattle.playBattleInConsole(
                SeededBattleGenerator.generateBattle(seed),
                new RandomChoiceAI(seed)
        );
    }
}
