package lemniscate.consolebattle.scripts.statistics;

import lemniscate.consolebattle.ConsoleBattle;
import lemniscate.consolebattle.SeededBattleGenerator;
import lemniscate.data.ai.RandomChoiceAI;
import lemniscate.engine.battle.Battle;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.data.FighterData;

import java.util.Random;

public class BattleStatistics {
    public static void main(String[] args) {
        // Constants
        final Random rng = new Random();
        final int BATTLE_COUNT = 10000;

        // Initialize tables
        StatisticTable<FighterData, Fighter> fighterTable = new StatisticTable<>("Fighters");
        fighterTable.setKeyNameMethod(fighterData -> fighterData.name);

        // Loop a lot of battles
        long startTime = System.currentTimeMillis();
        for (int i=0; i<BATTLE_COUNT; i++){
            long seed = rng.nextLong();
            Battle battle = SeededBattleGenerator.generateBattle(seed);

            ConsoleBattle.autoEvaluate(battle, new RandomChoiceAI(seed));

            // Add fighters to table
            for (Fighter fighter : battle.allFighters()){
                fighterTable.add(fighter.getData(), fighter);
            }
        }
        System.out.printf("evaluated %d battles in %.03fs%n", BATTLE_COUNT, (System.currentTimeMillis()-startTime)/1000.0);
        System.out.println();

        // Present the data
        fighterTable.printStats("Survival Rate",
                fighter -> (double) (fighter.isAlive() ? 100 : 0));
        fighterTable.printStats("Team Win Rate",
                fighter -> (double) (fighter.getTeam().hasLivingMembers() ? 100 : 0));
    }
}
