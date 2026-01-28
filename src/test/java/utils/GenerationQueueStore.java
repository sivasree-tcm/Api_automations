package utils;

import java.util.*;

public class GenerationQueueStore {

    // projectId -> (userId -> queueIds)
    private static final Map<Integer, Map<Integer, List<Integer>>> QUEUE_MAP = new HashMap<>();

    public static void store(Integer projectId, Integer userId, List<Integer> queueIds) {
        QUEUE_MAP
                .computeIfAbsent(projectId, k -> new HashMap<>())
                .put(userId, queueIds);
    }

    public static Map<Integer, Map<Integer, List<Integer>>> getAll() {
        return QUEUE_MAP;
    }

    public static void clear() {
        QUEUE_MAP.clear();
    }
}
