package lemniscate.engine.battle;

/** A response to a turn being submitted that gives information such as if it submitted successfully. **/
public class TurnResponse {
    /** Whether or not the turn request was accepted. **/
    public final boolean accepted;
    /** If not accepted, provides data on why it was not. **/
    public final String message;
    /** The resulting turn. Is null if accepted is false. **/
    public final BattleTurn turn;

    public TurnResponse(boolean accepted, String message, BattleTurn turn) {
        this.accepted = accepted;
        this.message = message;
        this.turn = turn;
    }

    public TurnResponse(String message) {
        this.accepted = false;
        this.message = message;
        this.turn = null;
    }

    @Override
    public String toString() {
        return message;
    }
}
