package utils;
import tests.roles.UserRole;

import api.login.LoginApi;
import io.restassured.response.Response;

import java.util.EnumMap;
import java.util.Map;

public class TokenUtil {

    private static final long TOKEN_VALIDITY =
            10 * 60 * 1000; // 10 minutesmport

    // Cache per role
    private static final Map<UserRole, String> tokenMap =
            new EnumMap<>(UserRole.class);

    private static final Map<UserRole, Integer> userIdMap =
            new EnumMap<>(UserRole.class);

    private static final Map<UserRole, Long> expiryMap =
            new EnumMap<>(UserRole.class);

    // ================== PUBLIC ==================

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

    // Backward compatibility (SUPER_ADMIN default)
    public static String getToken() {
        return getToken(UserRole.SUPER_ADMIN);
    }

    public static int getUserId() {
        return getUserId(UserRole.SUPER_ADMIN);
    }

    // ================== INTERNAL ==================

    private static boolean isExpired(UserRole role) {
        return System.currentTimeMillis() >
                expiryMap.getOrDefault(role, 0L);
    }

    private static synchronized void initLogin(UserRole role) {

        String email;
        String password;

        switch (role) {
            case ADMIN:
                email = ConfigReader.get("admin.email");
                password = ConfigReader.get("admin.password");
                break;

            case USER:
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
        expiryMap.put(
                role,
                System.currentTimeMillis() + TOKEN_VALIDITY
        );

        System.out.println(
                "Logged in as " + role +
                        " | userId = " + userIdMap.get(role)
        );
    }
}
