package api.generation;

import io.restassured.response.Response;
import utils.TokenUtil;
import tests.roles.UserRole;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UpdateTestCaseStepOrderApi {

    /**
     * POST /api/updateTestCaseStepOrder
     */
    public static Response updateStepOrder(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json");

        // 🔐 Auth handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth header
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .body(request)
                .post("/api/updateTestCaseStepOrder");
    }
}
