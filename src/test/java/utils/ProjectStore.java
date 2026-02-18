package utils;

import java.util.*;

public class ProjectStore {

    // ================= PROJECT LIST =================
    private static final Map<Integer, String> PROJECT_MAP = new LinkedHashMap<>();

    // ================= CURRENT PROJECT =================
    private static Integer selectedProjectId;

    // ================= THREAD-SAFE STORAGE =================
    private static final ThreadLocal<Integer> PROJECT_ID = new ThreadLocal<>();
    // ✅ ADDED: ThreadLocal for Project Name to ensure it's captured during creation
    private static final ThreadLocal<String> CURRENT_PROJECT_NAME = new ThreadLocal<>();

    // ✅ NEW: Framework configuration (REQUIRED BY ATS)
    private static String AUTOMATION_FRAMEWORK;
    private static String STORAGE_TYPE;

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
    // ✅ NEW: PROJECT NAME THREAD-LOCAL ACCESS
    // -------------------------------------------------
    public static void setProjectName(String name) {
        CURRENT_PROJECT_NAME.set(name);
    }

    // -------------------------------------------------
    // SELECTED PROJECT NAME (REQUIRED FOR ATS)
    // -------------------------------------------------
    public static String getSelectedProjectName() {
        if (selectedProjectId == null) {
            // Check ThreadLocal fallback if selectedProjectId isn't set yet
            return CURRENT_PROJECT_NAME.get();
        }
        return PROJECT_MAP.get(selectedProjectId);
    }

    // -------------------------------------------------
    // ✅ NEW: AUTOMATION FRAMEWORK
    // -------------------------------------------------
    public static void setAutomationFramework(String framework) {
        AUTOMATION_FRAMEWORK = framework;
        System.out.println("✅ Automation Framework set → " + framework);
    }

    public static String getAutomationFramework() {
        if (AUTOMATION_FRAMEWORK == null) {
            throw new RuntimeException("❌ Automation Framework not set in ProjectStore");
        }
        return AUTOMATION_FRAMEWORK;
    }

    // -------------------------------------------------
    // ✅ NEW: STORAGE TYPE
    // -------------------------------------------------
    public static void setStorageType(String storageType) {
        STORAGE_TYPE = storageType;
        System.out.println("✅ Storage Type set → " + storageType);
    }

    public static String getStorageType() {
        if (STORAGE_TYPE == null) {
            throw new RuntimeException("❌ Storage Type not set in ProjectStore");
        }
        return STORAGE_TYPE;
    }
    // -------------------------------------------------
    // PROJECT LIST
    // -------------------------------------------------
    public static void storeProjects(List<Map<String, Object>> response) {
        // Note: Removed .clear() logic to allow appending new projects during creation flows
        System.out.println("\n========== SYNCING PROJECT STORE ==========");

        for (Map<String, Object> obj : response) {
            Object idVal = obj.get("projectId");
            Object nameVal = obj.get("projectName");

            if (idVal != null && nameVal != null) {
                Integer id = Integer.parseInt(idVal.toString());
                String name = nameVal.toString();
                PROJECT_MAP.put(id, name);
                System.out.println("Project ID : " + id + " | Name : " + name);
            }
        }
    }

    public static boolean containsProject(Integer projectId) {
        return PROJECT_MAP.containsKey(projectId);
    }

    public static String getProjectName(Integer projectId) {
        String name = PROJECT_MAP.get(projectId);
        // Fallback to thread-local if map lookup fails
        return (name != null) ? name : CURRENT_PROJECT_NAME.get();
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
        CURRENT_PROJECT_NAME.remove(); // ✅ Added cleanup
    }

    public static void log() {
        System.out.println("Stored Projects: " + PROJECT_MAP);
        System.out.println("Selected ID: " + selectedProjectId);
        System.out.println("ThreadLocal ID: " + PROJECT_ID.get());
        System.out.println("ThreadLocal Name: " + CURRENT_PROJECT_NAME.get());
    }
}