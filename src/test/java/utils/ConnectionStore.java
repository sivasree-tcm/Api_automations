package utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ConnectionStore {

    private static final Map<String, Integer> CONNECTIONS = new ConcurrentHashMap<>();

    public static void store(String type, Integer connectionId) {
        CONNECTIONS.put(type, connectionId);
    }

    public static Integer get(String type) {
        return CONNECTIONS.get(type);
    }

    public static void clear() {
        CONNECTIONS.clear();
    }
}
