package api.project;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetTestCaseSummaryForTSApi {

    /**
     * Get Test Case Summary For Test Scenario
     * Endpoint: /api/getTestCaseSummaryForTestScenarioId
     */
    public static Response getTestCaseSummary(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json");

        // 🔐 Auth handling – SAME pattern
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth header
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "invalid_token");
        }
        else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        // 🔁 Extract required fields from request
        int testScenarioId =
                ((Number) ((java.util.Map<?, ?>) request)
                        .get("testScenarioId")).intValue();

        int userId =
                ((Number) ((java.util.Map<?, ?>) request)
                        .get("userId")).intValue();

        return req
                .body(
                        java.util.Map.of(
                                "testScenarioId", testScenarioId,
                                "userId", userId
                        )
                )
                .when()
                .post("/api/getTestCaseSummaryForTestScenarioId");
    }
}
