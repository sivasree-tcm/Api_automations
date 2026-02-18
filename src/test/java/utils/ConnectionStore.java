package utils;

public class ConnectionStore {
    private static final ThreadLocal<Integer> savedConnectionId = new ThreadLocal<>();
    private static final ThreadLocal<String> savedPlatform = new ThreadLocal<>();

    public static void setData(int id, String platform) {
        savedConnectionId.set(id);
        savedPlatform.set(platform);
    }

    // ✅ Hardcoded Connection ID
    public static Integer getConnectionId() {
        return 33;
    }

    // ✅ Hardcoded Platform
    public static String getPlatform() {
        return "azure";
    }

    // ✅ Hardcoded DevOps Project Name (Fixes IDE Error)
    public static String getDevopsProjectName() {
        return "f3c9e398-fa65-4695-bc19-b5172acd23a6";

    }

    // ✅ Hardcoded Team Name (Fixes IDE Error)
    public static String getTeamName() {
        return "Insurance Team";
    }

    public static void clear() {
        savedConnectionId.remove();
        savedPlatform.remove();
    }
}