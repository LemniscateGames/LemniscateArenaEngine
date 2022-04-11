package lemniscate.engine.battle.results;

import lemniscate.engine.battle.TurnEvent;

/** Just contains a message, for anything unimportant enough to not store anything else for (which is most events). **/
public class TurnEventMessage implements TurnEvent {
    private final String message;

    public TurnEventMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
