package api.project;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetLlmCredentialsApi {

    /**
     * GET LLM Credentials
     * Endpoint: /api/get-llm-credentials?userId=XX
     */
    public static Response getLlmCredentials(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json");

        // 🔐 Auth handling – SAME pattern as other APIs
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

        // Extract userId from request
        int userId = ((Number)
                ((java.util.Map<?, ?>) request).get("userId")
        ).intValue();

        return req
                .queryParam("userId", userId)
                .when()
                .get("/api/get-llm-credentials");
    }
}
