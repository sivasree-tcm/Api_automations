package api.login;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {

    public static Response login(String email, String password) {

        String payload = "{"
                + "\"userEmail\":\"" + email + "\","
                + "\"userPassword\":\"" + password + "\""
                + "}";

        return given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/api/login");
    }
}
