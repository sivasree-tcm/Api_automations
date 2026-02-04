package utils;

import java.util.*;

public class RoleStore {

    // projectId → list of roleIds
    private static final Map<Integer, List<Integer>> CREATED_ROLES = new HashMap<>();

    public static void store(Integer projectId, Integer roleId) {
        CREATED_ROLES
                .computeIfAbsent(projectId, k -> new ArrayList<>())
                .add(roleId);
    }

    public static List<Integer> getRoleIds(Integer projectId) {
        return CREATED_ROLES.getOrDefault(projectId, new ArrayList<>());
    }

    public static Map<Integer, List<Integer>> getAll() {
        return CREATED_ROLES;
    }

    public static void clear() {
        CREATED_ROLES.clear();
    }
}
