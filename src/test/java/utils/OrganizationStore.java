package utils;

public class OrganizationStore {

    private static Integer orgId;
    private static String orgName;

    public static void setOrganization(Integer id, String name) {
        orgId = id;
        orgName = name;
    }

    public static Integer getOrgId() {

        if (orgId == null) {
            throw new RuntimeException("❌ Organization ID not set.");
        }

        return orgId;
    }

    public static String getOrgName() {
        return orgName;
    }

    public static void clear() {
        orgId = null;
        orgName = null;
    }
}