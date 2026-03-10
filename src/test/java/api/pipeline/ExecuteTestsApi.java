package api.pipeline;

import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class ExecuteTestsApi {

    public static Response executeTests(Object request, String role) {
        return given()
                .relaxedHTTPSValidation() // Added for SSL stability
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken(tests.roles.UserRole.valueOf(role)))
                .body(request)
                .when()
                .post("/api/executeTests"); // Uses global Base URI
    }
}