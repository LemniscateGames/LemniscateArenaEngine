package lemniscate.engine.battle;

import java.util.List;
import java.util.Random;

public class BattleUtils {
    public static Fighter getRandomFighter(Random rng, List<Fighter> fighters){
        return fighters.get(rng.nextInt(fighters.size()));
    }
}
