package utils;

import java.util.*;

public class ProjectStore {

    private static final Map<Integer, String> PROJECT_MAP = new LinkedHashMap<>();

    public static void storeProjects(List<Map<String, Object>> response) {

        PROJECT_MAP.clear();
        System.out.println("\n========== PROJECT LIST ==========");

        for (Map<String, Object> obj : response) {
            Integer id = (Integer) obj.get("projectId");
            String name = (String) obj.get("projectName");

            if (id != null && name != null) {
                PROJECT_MAP.put(id, name);
                // ✅ Print project info
                System.out.println("Project ID : " + id + " | Project Name : " + name);
            }
        }
    }

    public static Integer getAnyProjectId() {
        return PROJECT_MAP.keySet().stream().findFirst().orElse(null);
    }

    public static String getProjectName(Integer projectId) {
        return PROJECT_MAP.get(projectId);
    }

    public static Map<Integer, String> getAllProjects() {
        return PROJECT_MAP;
    }
    public static Set<Integer> getAllProjectIds() {
        return PROJECT_MAP.keySet();
    }

    public static void clear() {
        PROJECT_MAP.clear();
    }


}
