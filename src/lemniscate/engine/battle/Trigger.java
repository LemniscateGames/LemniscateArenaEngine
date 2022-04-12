package lemniscate.engine.battle;

/** Something that can occur during battle that triggers effects on statuses. **/
public enum Trigger {
    /** Trigger before this fighter chooses their skill on their turn. **/
    TURN_START,
    /** Trigger after a fighter is finished with their turn,
     * after any skill was used during their turn or their turn was skipped. **/
    TURN_END,
    /** Trigger whenever there is a update to this fighter's stats.
     * This is where effects that change a fighter's stats should be placed for statuses that modify stats. **/
    STAT_UPDATE,
    /** Trigger before this fighter is attacked. **/
    BEFORE_ATTACKED,
    /** Trigger after this fighter is attacked. **/
    POST_ATTACKED,
    /** Trigger when this fighter is defeated. **/
    ON_DEFEAT
}
