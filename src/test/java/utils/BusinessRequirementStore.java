package utils;

import java.util.*;

public class BusinessRequirementStore {

    // projectId → list of brIds


        // projectId → list of brIds
        private static final Map<Integer, List<Integer>> BR_MAP = new HashMap<>();

        public static void store(Integer projectId, List<Integer> brIds) {
            BR_MAP.put(projectId, brIds);
        }

        // projectId → COMPLETED BR IDs
        private static final Map<Integer, List<Integer>> COMPLETED_BR_MAP =
                new LinkedHashMap<>();

        // ✅ NEW: Store completed BRs
        public static void storeCompletedBRs(Integer projectId, List<Integer> completedBrIds) {
            COMPLETED_BR_MAP.put(projectId, completedBrIds);
        }

        // ✅ Getter for completed BRs (used in next flow)
        public static List<Integer> getCompletedBRs(Integer projectId) {
            return COMPLETED_BR_MAP.getOrDefault(projectId, new ArrayList<>());
        }

        public static Map<Integer, List<Integer>> getAll() {
            return BR_MAP;
        }

        public static List<Integer> getBrIds(Integer projectId) {
            return BR_MAP.getOrDefault(projectId, new ArrayList<>());
        }

        public static void clear() {
            BR_MAP.clear();
        }

        // BRs actually sent for generation
        private static final Map<Integer, List<Integer>> GENERATED_BR_MAP =
                new HashMap<>();

        public static void storeGeneratedBRs(Integer projectId, List<Integer> brIds) {
            GENERATED_BR_MAP.put(projectId, new ArrayList<>(brIds));
        }

        public static List<Integer> getGeneratedBRs(Integer projectId) {
            return GENERATED_BR_MAP.getOrDefault(projectId, new ArrayList<>());
        }
    public static Integer getAnyBrId(Integer projectId) {

        // 1️⃣ Prefer BRs used for generation
        List<Integer> generated =
                GENERATED_BR_MAP.get(projectId);

        if (generated != null && !generated.isEmpty()) {
            return generated.get(0);
        }

        // 2️⃣ Fallback to completed BRs
        List<Integer> completed =
                COMPLETED_BR_MAP.get(projectId);

        if (completed != null && !completed.isEmpty()) {
            return completed.get(0);
        }

        // 3️⃣ Fallback to any fetched BR
        List<Integer> all =
                BR_MAP.get(projectId);

        if (all != null && !all.isEmpty()) {
            return all.get(0);
        }

        return null;
    }


}
