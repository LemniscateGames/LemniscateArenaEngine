package lemniscate.consolebattle.scripts.statistics;

import java.util.*;
import java.util.function.Function;

public class StatisticTable<K, V> {
    private final String name;
    private final Map<K, List<V>> table;
    private Function<K, String> keyNameMethod;
    private Function<V, Double> valueMethod;

    public StatisticTable(String name) {
        this.name = name;
        this.table = new HashMap<>();
    }

    // Table
    public Map<K, List<V>> getTable() {
        return table;
    }

    public void add(K key, V value){
        if (!table.containsKey(key)){
            table.put(key, new ArrayList<>());
        }
        table.get(key).add(value);
    }

    // Stats
    public double getAverage(K key, Function<V, Double> valueMethod){
        List<V> items = table.get(key);
        if (items.size() == 0) return 0;

        double total = 0;
        for (V item : items){
            total += valueMethod.apply(item);
        }
        return total / items.size();
    }


    // Display
    public void printStats(String header, Function<K, String> keyNameMethod, Function<V, Double> valueMethod){
        System.out.printf("==== %s of %s ====%n", header, name);
        for (K key : table.keySet()){
            System.out.printf("[%s] Average: %f%n",
                    keyNameMethod.apply(key),
                    getAverage(key, valueMethod)
            );
        }
        System.out.println();
    }
    public void printStats(String header, Function<V, Double> valueMethod){
        printStats(header, keyNameMethod, valueMethod);
    }

    // Accessors
    public String getName() {
        return name;
    }

    public Function<K, String> getKeyNameMethod() {
        return keyNameMethod;
    }

    public void setKeyNameMethod(Function<K, String> keyNameMethod) {
        this.keyNameMethod = keyNameMethod;
    }

    public Function<V, Double> getValueMethod() {
        return valueMethod;
    }

    public void setValueMethod(Function<V, Double> valueMethod) {
        this.valueMethod = valueMethod;
    }
}
