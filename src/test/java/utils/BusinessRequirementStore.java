package utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores Business Requirement (BR) data across different stages of the automation flow.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class BusinessRequirementStore {

    // ✅ Thread-safe maps to prevent ConcurrentModificationException
    // 1. All fetched BR IDs (Map: projectId -> list of brIds)
    private static final Map<Integer, List<Integer>> BR_MAP = new ConcurrentHashMap<>();

    // 2. BRs actually sent for generation (Map: projectId -> list of brIds)
    private static final Map<Integer, List<Integer>> GENERATED_BR_MAP = new ConcurrentHashMap<>();

    // 3. BRs that have completed processing/generation (Map: projectId -> list of brIds)
    private static final Map<Integer, List<Integer>> COMPLETED_BR_MAP = new ConcurrentHashMap<>();

    // ------------------------------------------------------------------------
    // STORAGE METHODS
    // ------------------------------------------------------------------------

    /**
     * Stores the initial list of BR IDs fetched for a project.
     */
    public static void store(Integer projectId, List<Integer> brIds) {
        if (projectId != null && brIds != null) {
            BR_MAP.put(projectId, new ArrayList<>(brIds));
        }
    }

    /**
     * Stores the BR IDs that were specifically selected/sent for generation.
     */
    public static void storeGeneratedBRs(Integer projectId, List<Integer> brIds) {
        if (projectId != null && brIds != null) {
            GENERATED_BR_MAP.put(projectId, new ArrayList<>(brIds));
        }
    }

    /**
     * Stores BR IDs that have reached 'Completed' status.
     */
    public static void storeCompletedBRs(Integer projectId, List<Integer> completedBrIds) {
        if (projectId != null && completedBrIds != null) {
            COMPLETED_BR_MAP.put(projectId, new ArrayList<>(completedBrIds));
        }
    }

    // ------------------------------------------------------------------------
    // GETTER METHODS
    // ------------------------------------------------------------------------

    /**
     * ✅ Added/Kept: To fix the "cannot resolve method getIds" error in tests.
     */
    public static List<Integer> getIds(Integer projectId) {
        return BR_MAP.getOrDefault(projectId, new ArrayList<>());
    }

    /**
     * ✅ Kept: Original method name.
     */
    public static List<Integer> getBrIds(Integer projectId) {
        return BR_MAP.getOrDefault(projectId, new ArrayList<>());
    }

    /**
     * ✅ Kept: For accessing generation-specific IDs.
     */
    public static List<Integer> getGeneratedBRs(Integer projectId) {
        return GENERATED_BR_MAP.getOrDefault(projectId, new ArrayList<>());
    }

    /**
     * ✅ Kept: For accessing completed-status IDs.
     */
    public static List<Integer> getCompletedBRs(Integer projectId) {
        return COMPLETED_BR_MAP.getOrDefault(projectId, new ArrayList<>());
    }

    /**
     * ✅ Kept: Returns the raw BR map.
     */
    public static Map<Integer, List<Integer>> getAll() {
        return BR_MAP;
    }

    // ------------------------------------------------------------------------
    // UTILITY METHODS
    // ------------------------------------------------------------------------

    /**
     * Picks a single BR ID for downstream testing based on a priority hierarchy:
     * 1. Generated BRs 2. Completed BRs 3. Any fetched BR.
     */
    public static Integer getAnyBrId(Integer projectId) {
        if (projectId == null) return null;

        // 1️⃣ Prefer BRs used for generation
        List<Integer> generated = GENERATED_BR_MAP.get(projectId);
        if (generated != null && !generated.isEmpty()) return generated.get(0);

        // 2️⃣ Fallback to completed BRs
        List<Integer> completed = COMPLETED_BR_MAP.get(projectId);
        if (completed != null && !completed.isEmpty()) return completed.get(0);

        // 3️⃣ Fallback to any fetched BR
        List<Integer> all = BR_MAP.get(projectId);
        if (all != null && !all.isEmpty()) return all.get(0);

        return null;
    }

    /**
     * Wipes all data across all maps for a fresh test run.
     */
    public static void clear() {
        BR_MAP.clear();
        GENERATED_BR_MAP.clear();
        COMPLETED_BR_MAP.clear();
    }
}