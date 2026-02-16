package utils;

import java.util.ArrayList;
import java.util.List;

public class GeneratedTSStore {

    private static final List<Integer> GENERATED_TS = new ArrayList<>();

    public static void store(List<Integer> tsIds) {
        GENERATED_TS.clear();
        GENERATED_TS.addAll(tsIds);
    }

    public static List<Integer> getAll() {
        return new ArrayList<>(GENERATED_TS);
    }

    public static boolean hasTS() {
        return !GENERATED_TS.isEmpty();
    }
}