package utils;

import java.util.*;

public class ProjectStore {

    private static final Map<Integer, String> PROJECT_MAP = new LinkedHashMap<>();
    private static Integer selectedProjectId; // ⭐ important

    // inside ProjectStore
    private static String USER_ID;

    public static void setUserId(String userId) {
        USER_ID = userId;
    }

    public static String getUserId() {
        return USER_ID;
    }
    // Store all projects
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

    public static void setSelectedProject(Integer projectId) {
        selectedProjectId = projectId;
    }

    // ✅ Get selected project
    public static Integer getSelectedProjectId() {
        if (selectedProjectId == null) {
            throw new RuntimeException("❌ No project selected");
        }
        return selectedProjectId;
    }

    // Optional helper
    public static boolean containsProject(Integer projectId) {
        return PROJECT_MAP.containsKey(projectId);
    }

    public static String getProjectName(Integer projectId) {
        return PROJECT_MAP.get(projectId);
    }

    public static Set<Integer> getAllProjectIds() {
        return PROJECT_MAP.keySet();
    }

    public static void clear() {
        PROJECT_MAP.clear();
        selectedProjectId = null;
    }

    public static Integer getAnyProjectId() {
        return PROJECT_MAP.keySet().stream().findFirst().orElse(null);
    }

        private static final ThreadLocal<Integer> projectId = new ThreadLocal<>();

        public static void setProjectId(int id) {
            projectId.set(id);
        }

        public static Integer getProjectId() {
            return projectId.get();
        }

    }

