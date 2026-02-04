package utils;

import java.util.Random;

public class RoleDataGenerator {

    private static final String[] ROLE_PREFIX = {
            "QA", "Tester", "Developer", "Manager",
            "Automation", "Support", "Analyst"
    };

    private static final String[] ROLE_SUFFIX = {
            "Lead", "Engineer", "User", "Admin", "Specialist"
    };

    private static final String[] DESC_PREFIX = {
            "Responsible for",
            "Handles",
            "Manages",
            "Works on",
            "Performs"
    };

    private static final String[] DESC_SUFFIX = {
            "automation tasks",
            "testing activities",
            "project operations",
            "quality assurance",
            "system validation"
    };

    private static final Random random = new Random();

    // ✅ Generates valid role name
    public static String generateRoleName() {
        return ROLE_PREFIX[random.nextInt(ROLE_PREFIX.length)]
                + "_"
                + ROLE_SUFFIX[random.nextInt(ROLE_SUFFIX.length)]
                + "_"
                + (100 + random.nextInt(900));
    }

    // ✅ Generates valid role description
    public static String generateRoleDescription() {
        return DESC_PREFIX[random.nextInt(DESC_PREFIX.length)]
                + " "
                + DESC_SUFFIX[random.nextInt(DESC_SUFFIX.length)];
    }
}
