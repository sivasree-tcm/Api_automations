package utils;

import java.util.*;

public class GenerationQueueStore {

    private static final Map<Integer, List<Integer>> QUEUE_MAP = new HashMap<>();

    public static Map<Integer, List<Integer>> getAll() {
        return QUEUE_MAP;
    }

    public static void store(Integer  userId, List<Integer> queueIds) {
        QUEUE_MAP.put(userId, queueIds);
    }

    public static List<Integer> getQueueIds(Integer  userId) {
        return QUEUE_MAP.getOrDefault(userId, new ArrayList<>());
    }

    public static void clear() {
        QUEUE_MAP.clear();
    }


}
