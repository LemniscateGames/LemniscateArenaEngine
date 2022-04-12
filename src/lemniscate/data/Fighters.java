package lemniscate.data;

import lemniscate.data.fighters.*;
import lemniscate.engine.data.FighterData;

/** Utility class that holds a bunch of StatusData static instances that are used across code
 * (except for StatModifier which contains a bunch of stat permutations) **/
public class Fighters {
    public static final FighterData[] fighterDatas = {
            new Collect(),
            new Unity(),

            new AmbulanceGuy(),
            new Romra(),
            new Xuirbo()
    };
}
