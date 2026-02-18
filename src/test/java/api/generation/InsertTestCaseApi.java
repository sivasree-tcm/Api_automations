package api.generation;

import io.restassured.response.Response;
import utils.TokenUtil;
import tests.roles.UserRole;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class InsertTestCaseApi {

    /**
     * POST /api/insertTC
     */
    public static Response insertTestCase(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json")
                .body(request);

        // 🔐 Auth handling (same pattern everywhere)
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
                .post("/api/insertTC");
    }
}
