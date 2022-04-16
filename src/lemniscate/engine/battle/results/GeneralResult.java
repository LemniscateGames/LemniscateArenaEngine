package lemniscate.engine.battle.results;

import lemniscate.engine.battle.Fighter;

/** Something to display to the screen that should have an identifier but does not need its own class because laziness.
 * Has one Fighter field and one int value field. **/
public class GeneralResult extends BattleResult {
    private final String id;
    private final Fighter fighter;
    private final int value;
    private final String message;

    public GeneralResult(String id, Fighter fighter, int value, String message) {
        this.id = id;
        this.fighter = fighter;
        this.value = value;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public Fighter getFighter() {
        return fighter;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
