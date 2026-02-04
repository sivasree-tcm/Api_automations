package utils;

import java.util.*;

public class TestScenarioStore {

    private static final Map<Integer, List<Integer>> BR_TS_MAP = new HashMap<>();

    public static void addTs(Integer brId, Integer tsId) {
        BR_TS_MAP
                .computeIfAbsent(brId, k -> new ArrayList<>())
                .add(tsId);
    }

    public static List<Integer> getTsByBr(Integer brId) {
        return BR_TS_MAP.getOrDefault(brId, Collections.emptyList());
    }

    public static Set<Integer> getAllBrIds() {
        return BR_TS_MAP.keySet();
    }

    public static void clear() {
        BR_TS_MAP.clear();
    }
    public static List<Integer> getTsForBr(Integer brId) {
        return BR_TS_MAP.getOrDefault(brId, Collections.emptyList());
    }
    public static boolean hasTS() {
        return !BR_TS_MAP.isEmpty();
    }
    public static List<Integer> getAllTsIds() {
        return BR_TS_MAP.values()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }
    public static List<Integer> getAllTS() {
        List<Integer> allTs = new ArrayList<>();
        for (List<Integer> tsList : BR_TS_MAP.values()) {
            allTs.addAll(tsList);
        }
        return allTs;
    }


}