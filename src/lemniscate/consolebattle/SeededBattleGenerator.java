package lemniscate.consolebattle;

import lemniscate.data.Fighters;
import lemniscate.engine.battle.Battle;
import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Team;
import lemniscate.local.LocalFighter;

import java.util.Random;

public class SeededBattleGenerator {
    private static Random rng;

    public static Battle generateBattle(long seed){
        rng = new Random(seed);

        Team allies = randomTeam(0, 4);
        Team enemies = randomTeam(0, 4);

        return new Battle(allies, enemies, seed);
    }

    public static Team randomTeam(int controllerId, int size){
        Team team = new Team(controllerId);
        for (int i=0; i<size; i++){
            team.addFighter(randomFighter());
        }
        return team;
    }

    public static Fighter randomFighter(){
        return new Fighter(
                new LocalFighter(
                        Fighters.fighterDatas[rng.nextInt(Fighters.fighterDatas.length)],
                        15, 25, rng
                )
        );
    }
}
