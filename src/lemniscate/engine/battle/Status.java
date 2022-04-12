package lemniscate.engine.battle;

import lemniscate.engine.battle.results.BattleResult;
import lemniscate.engine.data.StatusData;

/** A status instantiated on a Fighter. Stores an internal duration. **/
public class Status {
    /** The base data for the status this is an instance of. **/
    public final StatusData data;
    /** THe fighter this status effect is instantiated on. **/
    public final Fighter fighter;
    /** Current duration. **/
    private int duration;
    /** "Value" associated with this status.
     * Not used on most statuses, mainly used for things like barriers and vigor.
     */
    private int value;
    /** The fighter that inflicted this status. **/
    public final Fighter inflicter;

    // Constructors
    public Status(StatusData data, Fighter fighter, Fighter inflicter, int duration, int value) {
        this.data = data;
        this.fighter = fighter;
        this.inflicter = inflicter;
        this.duration = duration;
        this.value = value;
    }

    // Info
    public boolean isStatus(StatusData data){
        return data == this.data;
    }

    // Interaction
    public void remove(){
        fighter.removeStatus(this);
    }

    public void invoke(Fighter fighter, Trigger trigger){
        if (data.effects.containsKey(trigger)){
            data.effects.get(trigger).accept(fighter);
        }
    }

    // Apply an action, that may be modified.
    public void apply(BattleAction action){
        if (data.actions.containsKey(action.getClass())){
//            System.out.printf("(%s) PING! %s on %s%n", action, data.name, fighter);
            data.actions.get(action.getClass()).accept(action);
        }
    }

    // Accept a result, that cannot be modified.
    public void accept(BattleResult result){
        if (data.results.containsKey(result.getClass())){
            data.results.get(result.getClass()).accept(result);
        }
    }

    public void lowerDuration(int dur){
        duration -= dur;
        if (duration <= 0) this.remove();
    }

    public void onInflict(Fighter fighter){
        data.onInflict(fighter, this);
    }
    public void onRemove(Fighter fighter){
        data.onInflict(fighter, this);
    }

    // Accessors
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    // toString

    @Override
    public String toString() {
        return data.name;
    }
}
