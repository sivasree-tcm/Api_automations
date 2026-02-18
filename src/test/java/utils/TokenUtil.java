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
            System.out.println("⚠️ Token not cached or expired for " + role + " - generating new token");
            initLogin(role);
        } else {
            System.out.println("✅ Using cached token for " + role);
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

    public static String getToken(String roleString) {
        UserRole role = parseRole(roleString);
        return getToken(role);
    }

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
            return UserRole.SUPER_ADMIN;
        }

        try {
            String normalized = roleString.trim().toUpperCase().replace(" ", "_");

            switch (normalized) {
                case "SUPERADMIN":
                case "SUPER-ADMIN":
                    return UserRole.SUPER_ADMIN;

                case "ENDUSER":
                case "END-USER":
                case "USER":
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
        long currentTime = System.currentTimeMillis();
        long expiryTime = expiryMap.getOrDefault(role, 0L);
        boolean expired = currentTime > expiryTime;

        if (tokenMap.containsKey(role)) {
            long remainingTime = (expiryTime - currentTime) / 1000;
            if (remainingTime > 0) {
                System.out.println("🕒 Token for " + role + " valid for " + remainingTime + " more seconds");
            }
        }

        return expired;
    }

    private static synchronized void initLogin(UserRole role) {
        System.out.println("\n🔐 Initiating login for role: " + role);

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

        // ✅ CRITICAL: Extract token from Authorization HEADER (not body)
        String token = response.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            System.err.println("❌ ERROR: Authorization header is missing!");
            System.err.println("Response Status: " + response.getStatusCode());
            System.err.println("Response Headers: " + response.getHeaders());
            System.err.println("Response Body: " + response.getBody().asString());
            throw new RuntimeException("Authorization token not found in header for role: " + role);
        }

        // Extract userId from response body
        int userId;
        try {
            userId = response.jsonPath().getInt("userId");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not extract userId from response body");
            userId = 0; // Default value
        }

        // Store in cache
        tokenMap.put(role, token);
        userIdMap.put(role, userId);
        expiryMap.put(role, System.currentTimeMillis() + TOKEN_VALIDITY);

//        System.out.println("Auth token: " + token);

//        System.out.println("✅ Logged in as " + role + " | userId = " + userId);
        System.out.println("⏰ Token will expire in " + (TOKEN_VALIDITY / 1000) + " seconds\n");
    }

    // ================== UTILITY - CLEAR CACHE (FOR TESTING) ==================

    public static void clearToken(UserRole role) {
        tokenMap.remove(role);
        userIdMap.remove(role);
        expiryMap.remove(role);
        System.out.println("🔄 Cleared token cache for " + role);
    }

    public static void clearAllTokens() {
        tokenMap.clear();
        userIdMap.clear();
        expiryMap.clear();
        System.out.println("🔄 Cleared all token caches");
    }

    // ================== FORCE REFRESH TOKEN ==================

    public static synchronized void refreshToken() {
        refreshToken(UserRole.SUPER_ADMIN);
    }

    public static synchronized void refreshToken(UserRole role) {
        System.out.println("🔄 Forcing token refresh for " + role + "...");

        tokenMap.remove(role);
        expiryMap.remove(role);

        initLogin(role);

        System.out.println("✅ Token refreshed successfully for " + role);
    }
}