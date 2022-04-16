package lemniscate.engine;

import java.util.Random;

public class Formulas {
    public static int damage(double atk, double def, Random rng){
        return (int)( 2 * atk * ( atk / ( atk + def ) ) * (0.8 + 0.4*rng.nextDouble()) );
    }

    /** Get the effectiveness of this fighter. **/
    public static double effectiveness(int attackType, int defendType){
        // example
        // using water as attacking type
        // 0        1       2       3       4
        // Wind     Flame   Water   Thunder Stone
        // 0.75     2.0     1.0     0.5     1.25
        int difference = (attackType - defendType) % 5;
        switch (difference) {
            case 0: return 1.0;
            case 1: return 0.5;
            case 2: return 1.25;
            case 3: return 0.75;
            default: return 1.5;
        }
    }

    public static int statCurve(int stat, int rarity, int level){
        return (int)((stat * (1 + 0.04*rarity)) * Math.pow(1.05, level-20));
    }
}
