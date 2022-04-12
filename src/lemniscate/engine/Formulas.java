package lemniscate.engine;

public class Formulas {
    public static int damage(double atk, double def){
        return (int)( 2 * atk * ( atk / ( atk + def ) ) );
    }

    public static int statCurve(int stat, int level){
        return (int)(stat * Math.pow(1.05, level-20));
    }
}
