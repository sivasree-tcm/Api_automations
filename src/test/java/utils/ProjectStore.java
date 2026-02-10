package utils;

import java.util.*;

public class ProjectStore {

    // ================= PROJECT LIST =================
    private static final Map<Integer, String> PROJECT_MAP = new LinkedHashMap<>();

    // ================= CURRENT PROJECT =================
    private static Integer selectedProjectId;

    // ================= THREAD-SAFE PROJECT ID =================
    private static final ThreadLocal<Integer> PROJECT_ID = new ThreadLocal<>();

    // ================= USER =================
    private static String USER_ID;

    // -------------------------------------------------
    // USER
    // -------------------------------------------------
    public static void setUserId(String userId) {
        USER_ID = userId;
    }

    public static String getUserId() {
        return USER_ID;
    }

    // -------------------------------------------------
    // PROJECT LIST
    // -------------------------------------------------
    public static void storeProjects(List<Map<String, Object>> response) {

        PROJECT_MAP.clear();
        System.out.println("\n========== PROJECT LIST ==========");

        for (Map<String, Object> obj : response) {
            Integer id = (Integer) obj.get("projectId");
            String name = (String) obj.get("projectName");

            if (id != null && name != null) {
                PROJECT_MAP.put(id, name);
                System.out.println("Project ID : " + id + " | Name : " + name);
            }
        }
    }

    public static boolean containsProject(Integer projectId) {
        return PROJECT_MAP.containsKey(projectId);
    }

    public static String getProjectName(Integer projectId) {
        return PROJECT_MAP.get(projectId);
    }

    public static Set<Integer> getAllProjectIds() {
        return PROJECT_MAP.keySet();
    }

    public static Integer getAnyProjectId() {
        return PROJECT_MAP.keySet().stream().findFirst().orElse(null);
    }

    // -------------------------------------------------
    // SELECTED PROJECT (SAFE)
    // -------------------------------------------------
    public static void setSelectedProject(Integer projectId) {
        selectedProjectId = projectId;
    }

    /** ❌ DO NOT THROW HERE */
    public static Integer getSelectedProjectId() {
        return selectedProjectId;
    }

    public static Integer peekSelectedProjectId() {
        return selectedProjectId;
    }

    // -------------------------------------------------
    // THREAD-LOCAL PROJECT ID (used by API calls)
    // -------------------------------------------------
    public static void setProjectId(Integer id) {
        PROJECT_ID.set(id);
    }

    public static Integer getProjectId() {
        return PROJECT_ID.get();
    }

    // -------------------------------------------------
    // RESET
    // -------------------------------------------------
    public static void clear() {
        PROJECT_MAP.clear();
        selectedProjectId = null;
        PROJECT_ID.remove();
    }
}
