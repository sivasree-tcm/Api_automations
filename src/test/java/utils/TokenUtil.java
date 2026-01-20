package utils;

import api.login.LoginApi;
import io.restassured.response.Response;

public class TokenUtil {

    private static String token;
    private static int userId;
    private static long tokenExpiryTime; // epoch millis

    // token validity in millis (10 minutes)
    private static final long TOKEN_VALIDITY =
            10 * 60 * 1000;

    public static String getToken() {

        if (token == null || isTokenExpired()) {
            initLogin();
        }
        return token;
    }

    public static int getUserId() {

        if (userId == 0 || isTokenExpired()) {
            initLogin();
        }
        return userId;
    }

    private static boolean isTokenExpired() {
        return System.currentTimeMillis() > tokenExpiryTime;
    }

    private static synchronized void initLogin() {

        Response response = LoginApi.login(
                ConfigReader.get("login.email"),
                ConfigReader.get("login.password")
        );

        token = response.getHeader("Authorization");
        userId = response.jsonPath().getInt("userId");

        // Set expiry time (now + 10 mins)
        tokenExpiryTime =
                System.currentTimeMillis() + TOKEN_VALIDITY;

        System.out.println(
                "Logged in userId = " + userId +
                        " | Token refreshed at " + System.currentTimeMillis()
        );
    }
}
