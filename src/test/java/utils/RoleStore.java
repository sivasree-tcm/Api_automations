package utils;

import java.util.*;

public class RoleStore {

    // projectId → list of roleIds (original behavior)
    private static final Map<Integer, List<Integer>> CREATED_ROLES = new HashMap<>();

    // ⭐ Single active role (needed by your tests)
    private static Integer selectedRoleId;

    /* ------------------------------------------------
       MULTI-ROLE STORAGE (Existing Behavior)
       ------------------------------------------------ */

    public static synchronized void store(Integer projectId, Integer roleId) {

        CREATED_ROLES
                .computeIfAbsent(projectId, k -> new ArrayList<>())
                .add(roleId);
    }

    public static synchronized List<Integer> getRoleIds(Integer projectId) {
        return CREATED_ROLES.getOrDefault(projectId, new ArrayList<>());
    }

    public static synchronized Map<Integer, List<Integer>> getAll() {
        return CREATED_ROLES;
    }

    /* ------------------------------------------------
       SINGLE SELECTED ROLE (NEW – FIXES YOUR ERROR)
       ------------------------------------------------ */

    public static synchronized void setRoleId(Integer roleId) {
        selectedRoleId = roleId;
        System.out.println("✅ Selected Role ID set to → " + roleId);
    }

    public static synchronized Integer getRoleId() {

        if (selectedRoleId == null) {
            throw new RuntimeException("❌ No role selected in RoleStore");
        }

        return selectedRoleId;
    }

    /* ------------------------------------------------ */

    public static synchronized void clear() {
        CREATED_ROLES.clear();
        selectedRoleId = null;
    }
}
