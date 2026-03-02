package utils;

import java.util.*;
import java.util.stream.Collectors;

public class TestScenarioStore {

    // ✅ Using LinkedHashMap to preserve insertion order
    private static final Map<Integer, List<Integer>> BR_TS_MAP = new LinkedHashMap<>();

    /**
     * Adds a Test Scenario ID linked to a Business Requirement ID.
     */
    public static void addTs(Integer brId, Integer tsId) {
        BR_TS_MAP
                .computeIfAbsent(brId, k -> new ArrayList<>())
                .add(tsId);
    }

    /**
     * ✅ Gets the very last Test Scenario ID that was added across all BRs.
     * Useful for 'Update Last' or 'Delete Last' operations.
     */
    public static Integer getLastTsId() {
        if (BR_TS_MAP.isEmpty()) {
            return null;
        }

        // Get the last entry set added to the LinkedHashMap
        List<List<Integer>> allValueLists = new ArrayList<>(BR_TS_MAP.values());
        List<Integer> lastList = allValueLists.get(allValueLists.size() - 1);

        if (lastList.isEmpty()) {
            return null;
        }

        // Return the last element of the last list
        return lastList.get(lastList.size() - 1);
    }

    /**
     * Gets all Test Scenario IDs for a specific Business Requirement.
     */
    public static List<Integer> getTsByBr(Integer brId) {
        return BR_TS_MAP.getOrDefault(brId, Collections.emptyList());
    }

    /**
     * Returns true if any Test Scenarios have been stored.
     */
    public static boolean hasTS() {
        return !BR_TS_MAP.isEmpty();
    }

    /**
     * Returns a unique list of all Test Scenario IDs stored.
     */
    public static List<Integer> getAllTsIds() {
        return BR_TS_MAP.values()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Gets all BR IDs currently in the store.
     */
    public static Set<Integer> getAllBrIds() {
        return BR_TS_MAP.keySet();
    }

    /**
     * Clears all stored data.
     */
    public static void clear() {
        BR_TS_MAP.clear();
    }

    /**
     * Alias for getTsByBr.
     */
    public static List<Integer> getTsForBr(Integer brId) {
        return getTsByBr(brId);
    }
}