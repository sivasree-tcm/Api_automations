package utils;
import tests.roles.UserRole;

import api.login.LoginApi;
import io.restassured.response.Response;

import java.util.EnumMap;
import java.util.Map;

public class TokenUtil {

    private static final long TOKEN_VALIDITY = 10 * 60 * 1000; // 10 minutes

    // Cache per role
    private static final Map<UserRole, String> tokenMap = new EnumMap<>(UserRole.class);
    private static final Map<UserRole, Integer> userIdMap = new EnumMap<>(UserRole.class);
    private static final Map<UserRole, Long> expiryMap = new EnumMap<>(UserRole.class);

    // ================== PUBLIC - ENUM VERSION ==================

    public static String getToken(UserRole role) {
        if (!tokenMap.containsKey(role) || isExpired(role)) {
            initLogin(role);
        }
        return tokenMap.get(role);
    }

    public static int getUserId(UserRole role) {
        if (!userIdMap.containsKey(role) || isExpired(role)) {
            initLogin(role);
        }
        return userIdMap.get(role);
    }

    // ================== PUBLIC - STRING VERSION (FOR FLEXIBILITY) ==================

    /**
     * Get token by role name (String)
     * @param roleString - "SUPER_ADMIN", "ADMIN", or "END_USER"
     */
    public static String getToken(String roleString) {
        UserRole role = parseRole(roleString);
        return getToken(role);
    }

    /**
     * Get userId by role name (String)
     * @param roleString - "SUPER_ADMIN", "ADMIN", or "END_USER"
     */
    public static int getUserId(String roleString) {
        UserRole role = parseRole(roleString);
        return getUserId(role);
    }

    // ================== BACKWARD COMPATIBILITY (SUPER_ADMIN DEFAULT) ==================

    public static String getToken() {
        return getToken(UserRole.SUPER_ADMIN);
    }

    public static int getUserId() {
        return getUserId(UserRole.SUPER_ADMIN);
    }

    // ================== HELPER - PARSE STRING TO ENUM ==================

    private static UserRole parseRole(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return UserRole.SUPER_ADMIN; // Default
        }

        try {
            // Handle common variations
            String normalized = roleString.trim().toUpperCase().replace(" ", "_");

            // Map common variations to enum values
            switch (normalized) {
                case "SUPERADMIN":
                case "SUPER-ADMIN":
                    return UserRole.SUPER_ADMIN;

                case "ENDUSER":
                case "END-USER":
                case "USER":  // Map "USER" to END_USER
                    return UserRole.END_USER;

                default:
                    return UserRole.valueOf(normalized);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role: " + roleString + ". Defaulting to SUPER_ADMIN");
            return UserRole.SUPER_ADMIN;
        }
    }

    // ================== INTERNAL ==================

    private static boolean isExpired(UserRole role) {
        return System.currentTimeMillis() > expiryMap.getOrDefault(role, 0L);
    }

    private static synchronized void initLogin(UserRole role) {

        String email;
        String password;

        switch (role) {
            case ADMIN:
                email = ConfigReader.get("admin.email");
                password = ConfigReader.get("admin.password");
                break;

            case END_USER:
                email = ConfigReader.get("user.email");
                password = ConfigReader.get("user.password");
                break;

            case SUPER_ADMIN:
            default:
                email = ConfigReader.get("superadmin.email");
                password = ConfigReader.get("superadmin.password");
        }

        Response response = LoginApi.login(email, password);

        tokenMap.put(role, response.getHeader("Authorization"));
        userIdMap.put(role, response.jsonPath().getInt("userId"));
        expiryMap.put(role, System.currentTimeMillis() + TOKEN_VALIDITY);

        System.out.println("✅ Logged in as " + role + " | userId = " + userIdMap.get(role));
    }

    // ================== UTILITY - CLEAR CACHE (FOR TESTING) ==================

    /**
     * Clear cached token for a specific role (useful for testing token expiry)
     */
    public static void clearToken(UserRole role) {
        tokenMap.remove(role);
        userIdMap.remove(role);
        expiryMap.remove(role);
        System.out.println("🔄 Cleared token cache for " + role);
    }

    /**
     * Clear all cached tokens
     */
    public static void clearAllTokens() {
        tokenMap.clear();
        userIdMap.clear();
        expiryMap.clear();
        System.out.println("🔄 Cleared all token caches");
    }
}