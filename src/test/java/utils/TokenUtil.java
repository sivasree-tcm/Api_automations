package utils;

import api.login.LoginApi;
import io.restassured.response.Response;

public class TokenUtil {

    private static String token;
    private static int userId;

    public static String getToken() {
        if (token == null) {
            initLogin();
        }
        return token;
    }

    public static int getUserId() {
        if (userId == 0) {
            initLogin();
        }
        return userId;
    }

    private static void initLogin() {
        Response response = LoginApi.login(
                ConfigReader.get("login.email"),
                ConfigReader.get("login.password")
        );

        token = response.getHeader("Authorization");
        userId = response.jsonPath().getInt("userId");

        System.out.println("Logged in userId = " + userId);
    }
}
