package lemniscate.engine.data;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.TurnRequest;

public interface FighterAI {
    public abstract TurnRequest decide(Fighter actor);
}
