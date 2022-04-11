package lemniscate.engine.battle;

public interface TurnEvent {
    /** A brief textual description of what happened in this event.
     * Used for logging a battle's events in a text field or console.
     */
    String toString();
}
