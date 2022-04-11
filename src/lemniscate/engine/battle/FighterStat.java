package lemniscate.engine.battle;

/** A stat on a fighter that contains its increase values for all magnitudes (see data.statuses.StatModifier). **/
public class FighterStat {
    public enum OperType {
        /** Add directly to the value **/
        ADD,
        /** Add a percentage of the base stat to the final value. **/
        ADD_PERCENT
    }
    /** The type of operation to perform on the final stat. **/
    public OperType operType;

    /** Percentage changes for each magnitude (Increased, Greatly Increased, Decreased, Greatly Decreased) **/
    public final double[] magnitudeChanges;

    public FighterStat(OperType operType, double... magnitudeChanges) {
        this.operType = operType;
        this.magnitudeChanges = magnitudeChanges;
    }
}
