package lemniscate.data.statuses;

import lemniscate.engine.battle.Fighter;
import lemniscate.engine.battle.FighterStat;
import lemniscate.engine.battle.Status;
import lemniscate.engine.data.StatusData;
import lemniscate.engine.StatusType;
import lemniscate.engine.battle.Trigger;

public class StatModifier extends StatusData {
    public enum Stat {
        ATTACK("Attack", FighterStat.OperType.ADD_PERCENT, 0.5, 0.75, -0.3, -0.45),
        DEFENSE("Defense", FighterStat.OperType.ADD_PERCENT, 0.5, 0.75, -0.3, -0.45),
        SPEED("Speed", FighterStat.OperType.ADD_PERCENT, 0.25, 0.4, -0.2, -0.3),
        CRIT_CHANCE("Critical Chance", FighterStat.OperType.ADD, 0.2, 0.4, -0.2, -0.4),
        CRIT_DAMAGE("Critical Damage", FighterStat.OperType.ADD, 0.2, 0.4, -0.2, -0.4),
        EVASION("Evasion", FighterStat.OperType.ADD, 0.2, 0.4, -0.3, -0.6),
        RESISTANCE("Resistance", FighterStat.OperType.ADD, 0.15, 0.25, -0.2, -0.35);

        String name;
        FighterStat fighterStat;
        Stat(String name, FighterStat.OperType operType, double... magnitudeChanges) {
            this.name = name;
            this.fighterStat = new FighterStat(operType, magnitudeChanges);
        }

        double changeFor(Magnitude magnitude){
            int changeIndex;
            switch(magnitude){
                case INCREASED: changeIndex = 0; break;
                case GREATLY_INCREASED: changeIndex = 1; break;
                case DECREASED: changeIndex = 2; break;
                default: changeIndex = 3;
            }
            return fighterStat.magnitudeChanges[changeIndex];
        }
    }
    /** The stat this stat modifier is modifying. **/
    private final Stat stat;

    public enum Magnitude {
        INCREASED("Increased"),
        GREATLY_INCREASED("Greatly Increased"),
        DECREASED("Decreased"),
        GREATLY_DECREASED("Greatly Decreased");

        String prefix;
        Magnitude(String prefix) {
            this.prefix = prefix;
        }
    }
    /** The magnitude that the stat is changing by. Value differs between stats based on this magnitude. **/
    private final Magnitude magnitude;

    // Constructor
    public StatModifier(Stat stat, Magnitude magnitude) {
        super(
                magnitude.prefix + " " + stat.name,
                (magnitude == Magnitude.INCREASED || magnitude == Magnitude.GREATLY_INCREASED)
                ? StatusType.POSITIVE : StatusType.NEGATIVE,
                String.format("%s %s by %s.",
                        (magnitude == Magnitude.INCREASED || magnitude == Magnitude.GREATLY_INCREASED)
                                ? "Increases" : "Decreases",
                        stat.name,
                        stat.changeFor(magnitude)
                                * ((magnitude == Magnitude.INCREASED || magnitude == Magnitude.GREATLY_INCREASED)
                                ? 1 : -1))
        );
        this.stat = stat;
        this.magnitude = magnitude;

        addEffect(Trigger.STAT_UPDATE, this::update);

        addEffect(Trigger.TURN_END, this::tickDuration);
    }

    @Override
    public void onInflict(Fighter fighter, Status status) {
        fighter.updateStats();
    }
    @Override
    public void onRemove(Fighter fighter, Status status) {
        fighter.updateStats();
    }

    /** Method called when a stat update occurs. **/
    public void update(Fighter fighter){
        double change = stat.changeFor(magnitude);

        switch(stat){
            case ATTACK:
                fighter.addAtk(stat.fighterStat.operType == FighterStat.OperType.ADD_PERCENT
                        ? change * fighter.source.getAtk() : change); break;
            case DEFENSE:
                fighter.addDef(stat.fighterStat.operType == FighterStat.OperType.ADD_PERCENT
                        ? change * fighter.source.getDef() : change); break;
            case SPEED:
                fighter.addSpd(stat.fighterStat.operType == FighterStat.OperType.ADD_PERCENT
                        ? change * fighter.source.getSpd() : change); break;
        }
    }
}
