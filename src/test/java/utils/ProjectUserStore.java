package utils;

import java.util.*;

public class ProjectUserStore {

    // projectId -> List of user objects
    private static final Map<Integer, List<Map<String, Object>>> PROJECT_USERS =
            new LinkedHashMap<>();

    public static void storeUsers(Integer projectId, List<Map<String, Object>> users) {

        if (users == null || users.isEmpty()) {
            System.out.println("⚠ No users found for projectId: " + projectId);
            return;
        }

        PROJECT_USERS.put(projectId, users);

        // ✅ Print stored users
        System.out.println("\nProject ID: " + projectId);
        for (Map<String, Object> user : users) {
            System.out.println(
                    "User ID: " + user.get("userId") +
                            ", Name: " + user.get("userFirstName") + " " + user.get("userLastName")
            );
        }
    }

    // ✅ THIS IS WHAT YOU NEED
    public static List<Map<String, Object>> getUsers(Integer projectId) {
        return PROJECT_USERS.getOrDefault(projectId, new ArrayList<>());
    }

    public static void clear() {
        PROJECT_USERS.clear();
    }

}
