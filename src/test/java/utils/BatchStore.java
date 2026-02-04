package utils;

import java.util.*;

public class BatchStore {

    private static final Set<String> BATCH_IDS = new HashSet<>();

    public static void addBatch(String batchId) {
        BATCH_IDS.add(batchId);
    }
    public static void addBatchId(String batchId) {
        if (batchId != null && !batchId.isBlank()) {
            BATCH_IDS.add(batchId);
        }
    }

    public static Set<String> getBatchIds() {
        return BATCH_IDS;
    }
    public static boolean hasBatchIds() {
        return !BATCH_IDS.isEmpty();
    }



}