package utils;

public class RoleStore {

    private static Integer roleId;

    public static void setRoleId(Integer id) {
        roleId = id;
    }

    public static Integer getRoleId() {
        if (roleId == null) {
            throw new IllegalStateException("❌ Role ID not set in RoleStore");
        }
        return roleId;
    }
}
