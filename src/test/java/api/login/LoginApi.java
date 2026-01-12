package api.login;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class LoginApi {

    public static Response login(String email, String password) {

        String payload = "{\n" +
                "  \"userEmail\": \"" + email + "\",\n" +
                "  \"userPassword\": \"" + password + "\"\n" +
                "}";

        return given()
                .contentType("application/json")
                .body(payload)
                .post("/api/login");
    }
}
