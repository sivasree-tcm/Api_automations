package api.connection;

import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class ExecuteTestsApi {

    public static Response executeTests(Object request, String role) {
        return given()
                .baseUri("https://test.tsigma.ai")
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken(tests.roles.UserRole.valueOf(role)))
                .body(request)
                .when()
                .post("/api/executeTests");
    }
}