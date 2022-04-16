package lemniscate.engine.battle;

public abstract class TurnEvent {
    private String message;

    /** When this event is initialized, the battle calls this message to store its value by calling toString
     * on this turnEvent and storing it here. THis is to prevent it from displaying information withoutdated info.
     * The most common example of this before it was changed was statuses displaying the wrong duration.
     */
    public void storeMessage(){
        this.message = toString();
    }

    public String getMessage() {
        return message;
    }
}
