package utils;

import api.login.LoginApi;
import io.restassured.response.Response;

public class TokenUtil {

    private static String token;

    public static String getToken() {
        if (token == null) {
            Response response = LoginApi.login(
                    ConfigReader.get("login.email"),
                    ConfigReader.get("login.password")
            );
            token = response.getHeader("Authorization");
        }
        return token;
    }
}
