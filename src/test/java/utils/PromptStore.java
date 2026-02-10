package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PromptStore {

    private static final Map<String, Integer> promptIds =
            new ConcurrentHashMap<>();

    // Save prompt by type (BR_TO_TS, TS_TO_TC)
    public static void setPromptId(String type, Integer id) {
        promptIds.put(type, id);
    }

    // Get prompt by type
    public static Integer getPromptId(String type) {
        return promptIds.get(type);
    }

    public static boolean hasPromptId(String type) {
        return promptIds.containsKey(type);
    }

    public static void clear() {
        promptIds.clear();
    }
}
