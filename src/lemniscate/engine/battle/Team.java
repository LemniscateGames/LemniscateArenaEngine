package lemniscate.engine.battle;

import java.util.ArrayList;
import java.util.List;

/** A team of BattleFighters in a Battle instance. **/
public class Team {
    /** The ID of the player that controls fighters in this team. 0 for computer. **/
    private int controllerId;

    /** All fighters in this team. **/
    private final List<Fighter> fighters;

    /** The LE this team has generated (one tenth = 1). Max is 100 to represent 10 LE. **/
    private int le;

    // ======== Constructors
    public Team(int controllerId) {
        this.controllerId = controllerId;
        this.fighters = new ArrayList<>();
    }
    public Team() {
        this(0);
    }
    public Team(int controllerId, Fighter... fighters) {
        this(controllerId);
        for (Fighter fighter : fighters){
            addFighter(fighter);
        }
    }
    public Team(Fighter... fighters) {
        this();
        for (Fighter fighter : fighters){
            addFighter(fighter);
        }
    }

    // Info
    public boolean canLeBoost(Skill skill){
        return skill.isLeBoostable() && skill.getLeCost() <= le;
    }

    /** If this team has at least one living fighter. **/
    public boolean hasLivingMembers(){
        for (Fighter fighter : fighters){
            if (fighter.isAlive()) {
                return true;
            }
        }
        return false;
    }

    // ======== Accessors
    public void addFighter(Fighter fighter){
        fighters.add(fighter);
        fighter.setTeam(this);
    }

    public List<Fighter> getFighters() {
        return fighters;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public int getLe() {
        return le;
    }

    public void setLe(int le) {
        this.le = le;
    }
}
