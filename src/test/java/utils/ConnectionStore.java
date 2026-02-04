package utils;

public class ConnectionStore {
    private static final ThreadLocal<Integer> savedConnectionId = new ThreadLocal<>();
    private static final ThreadLocal<String> savedPlatform = new ThreadLocal<>();

    public static void setData(int id, String platform) {
        savedConnectionId.set(id);
        savedPlatform.set(platform);
    }

    public static Integer getConnectionId() { return savedConnectionId.get(); }
    public static String getPlatform() { return savedPlatform.get(); }

    public static void clear() {
        savedConnectionId.remove();
        savedPlatform.remove();
    }
}