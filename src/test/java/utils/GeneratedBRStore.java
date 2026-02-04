package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneratedBRStore {

    private static final List<Integer> GENERATED_BRS = new ArrayList<>();

    public static void store(List<Integer> brIds) {
        GENERATED_BRS.clear();
        GENERATED_BRS.addAll(brIds);
    }

    public static List<Integer> getBrIds() {
        return Collections.unmodifiableList(GENERATED_BRS);
    }

    public static boolean hasBrs() {
        return !GENERATED_BRS.isEmpty();
    }

    public static void clear() {
        GENERATED_BRS.clear();
    }
}