package lemniscate.engine.data;

import java.util.HashMap;
import java.util.Map;

public class SkillParams {
    public final Map<String, Integer> ints;
    public final Map<String, Double> doubles;

    public SkillParams() {
        ints = new HashMap<>();
        doubles = new HashMap<>();
    }

    public void put(String key, int value){
        ints.put(key, value);
    }
    public void put(String key, double value){
        doubles.put(key, value);
    }
}
