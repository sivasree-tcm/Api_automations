package api.generation;

import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AddTestScenarioApi {

    public static Response addTestScenario(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json")
                .body(request);

        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .when()
                .post("/api/addTestScenario");
    }
}
