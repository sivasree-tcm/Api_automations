package api.user;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RegisterUserApi {

    public static Response registerUser(Object requestBody) {

        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/registerUser");
    }
}
