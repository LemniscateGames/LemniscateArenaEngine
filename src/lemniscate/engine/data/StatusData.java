package lemniscate.engine.data;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.Status;
import lemniscate.engine.battle.Trigger;
import lemniscate.engine.battle.BattleAction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/** A status effect that can exist on a fighter.
 * Contains a set of triggers and effects that run when that event occurs.
 * Many have a duration that ticks down each turn, and the status effect is cleared when it reaches 0.
 * Some status effects are invisible to the player.
 */
public abstract class StatusData {
    /** Display name of this status. **/
    public final String name;
    /** Brief description of this status. **/
    public final String description;
    /** Type of this status. 1=positive/buff, 0=neutral, -1=negative/debuff. **/
    public final int type;
    /** Map of effects to run when certain events occur. **/
    public final Map<Trigger, Consumer<Fighter>> effects;
    /** Map of effects to run on certain actions when they occur. **/
    public final Map<Class<? extends BattleAction>, Function<BattleAction, BattleAction>> actions;

    /** Whether this status effect uses duration or it lasts infinitely until cleared. **/
    private boolean usesDuration = true;

    // Constructors
    public StatusData(String name, int type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;

        this.effects = new HashMap<>();
        this.actions = new HashMap<>();
    }

    // On infliction & removal defaults, can be overridden if needed
    public void onInflict(Fighter fighter, Status status){

    }
    public void onRemove(Fighter fighter, Status status){

    }

    // Effect adding
    public void addEffect(Trigger trigger, Consumer<Fighter> effect){
        effects.put(trigger, effect);
    }

    public <T extends BattleAction> void addAction(Class<T> actionClass, Function<T, T> effect){
        actions.put(actionClass, (Function<BattleAction, BattleAction>) effect);
    }

    // Other
    public void tickDuration(Fighter fighter){
        fighter.getStatus().lowerDuration(1);
    }

    // Accessors
    public boolean usesDuration() {
        return usesDuration;
    }

    public void setUsesDuration(boolean usesDuration) {
        this.usesDuration = usesDuration;
    }
}
