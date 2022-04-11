package lemniscate.engine;

import lemniscate.engine.battle.Fighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TargetType {
    NONE,
    SELF,
    ONE_ENEMY, ALL_ENEMIES, RANDOM_ENEMY, ONE_ENEMY_PLUS_RANDOM,
    ONE_ALLY, ALL_ALLIES;

    public List<Fighter> getPossibleTargets(Fighter fighter){
        switch(this){
            case SELF:
                return fighterList(fighter);
            case ONE_ENEMY: case ALL_ENEMIES: case RANDOM_ENEMY: case ONE_ENEMY_PLUS_RANDOM:
                return fighter.enemies(true);
            case ONE_ALLY: case ALL_ALLIES:
                return fighter.allies(true, true);
            default:
                return new ArrayList<>();
        }
    }

    public List<Fighter> getTargets(Fighter fighter){
        switch(this){
            case SELF: case ONE_ENEMY: case ONE_ALLY:
                return fighterList(fighter.getTarget());
            case ALL_ENEMIES:
                return fighter.enemies();
            case ALL_ALLIES:
                return fighter.allies();
            case RANDOM_ENEMY:
                return fighterList(Utils.randomChoice(fighter.getBattle().rng, fighter.enemies()));
            case ONE_ENEMY_PLUS_RANDOM:
                Fighter target = fighter.getTarget();
                List<Fighter> otherEnemies = new ArrayList<>(fighter.enemies());
                otherEnemies.remove(target);
                return fighterList(target, Utils.randomChoice(fighter.getBattle().rng, otherEnemies));
            default:
                return new ArrayList<>();
        }
    }

    public static List<Fighter> fighterList(Fighter... fighters){
        return Arrays.asList(fighters);
    }
}
