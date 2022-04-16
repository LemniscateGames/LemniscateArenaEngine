package lemniscate.consolebattle.scripts;

import lemniscate.consolebattle.ConsoleBattle;
import lemniscate.consolebattle.SeededBattleGenerator;
import lemniscate.data.ai.RandomChoiceAI;
import lemniscate.engine.battle.Battle;

import java.util.Random;

public class RandomSeedBattle {
    public static void main(String[] args) {
        Random rng = new Random();
        long seed = rng.nextLong();

        Battle battle = SeededBattleGenerator.generateBattle(seed);
        ConsoleBattle.displayBattle(battle);
        ConsoleBattle.playBattleInConsole(battle, new RandomChoiceAI(seed));
    }
}
