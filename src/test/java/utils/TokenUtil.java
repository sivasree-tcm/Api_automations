package utils;

import tests.roles.UserRole;
import api.login.LoginApi;
import io.restassured.response.Response;

import java.util.EnumMap;
import java.util.Map;

public class TokenUtil {

    private static final long TOKEN_VALIDITY = 10 * 60 * 1000; // 10 minutes

    private static final Map<UserRole, String> tokenMap = new EnumMap<>(UserRole.class);
    private static final Map<UserRole, Integer> userIdMap = new EnumMap<>(UserRole.class);
    private static final Map<UserRole, Long> expiryMap = new EnumMap<>(UserRole.class);

    /* ================= PUBLIC ================= */

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

    public static String getToken(String roleString) {
        return getToken(parseRole(roleString));
    }

    public static int getUserId(String roleString) {
        return getUserId(parseRole(roleString));
    }

    public static String getToken() {
        return getToken(UserRole.SUPER_ADMIN);
    }

    public static int getUserId() {
        return getUserId(UserRole.SUPER_ADMIN);
    }

    /* ================= INTERNAL ================= */

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

        /* 🔴 HARD STOP IF LOGIN FAILS */
        if (response.getStatusCode() != 200) {
            throw new RuntimeException(
                    "❌ LOGIN FAILED for role=" + role +
                            " | Status=" + response.getStatusCode() +
                            " | Response=" + response.asPrettyString()
            );
        }

        /* ✅ Extract token safely */
        String token =
                response.getHeader("Authorization") != null
                        ? response.getHeader("Authorization")
                        : response.jsonPath().getString("token");

        if (token == null || token.isBlank()) {
            throw new RuntimeException("❌ Token missing in login response for role=" + role);
        }

        int userId = response.jsonPath().getInt("userId");

        tokenMap.put(role, token);
        userIdMap.put(role, userId);
        expiryMap.put(role, System.currentTimeMillis() + TOKEN_VALIDITY);

        System.out.println("✅ Logged in successfully as " + role + " | userId=" + userId);
    }

    /* ================= ROLE PARSER ================= */

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
        } catch (Exception e) {
            System.err.println("⚠ Invalid role: " + roleString + ", defaulting to SUPER_ADMIN");
            return UserRole.SUPER_ADMIN;
        }
    }

    /* ================= UTIL ================= */

    public static void clearAllTokens() {
        tokenMap.clear();
        userIdMap.clear();
        expiryMap.clear();
        System.out.println("🔄 Cleared all token caches");
    }

    public static void refreshToken(UserRole role) {
        tokenMap.remove(role);
        userIdMap.remove(role);
        expiryMap.remove(role);
        initLogin(role);
    }
}
