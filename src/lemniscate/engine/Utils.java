package lemniscate.engine;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Utils {
//    public static <T> T randomChoice(T[] arr){
//        return arr[(int)(Math.random() * arr.length)];
//    }
//
//    public static <T> T randomChoice(List<T> list){
//        return list.get((int)(Math.random() * list.size()));
//    }

    public static <T> T randomChoice(Random rng, T[] arr){
        return arr[rng.nextInt(arr.length)];
    }

    public static <T> T randomChoice(Random rng, List<T> list){
        return list.get(rng.nextInt(list.size()));
    }

    /** Returns true if all items in c1 are present in c2, and all items in c2 are present in c1. **/
    public static <T> boolean allItemsShared(Collection<T> c1, Collection<T> c2){
        for (T t : c1){
            if (!c2.contains(t)) return false;
        }
        for (T t : c2){
            if (!c1.contains(t)) return false;
        }
        return true;
    }
}
