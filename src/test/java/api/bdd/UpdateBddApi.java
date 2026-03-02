package api.bdd;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UpdateBddApi {

    public static Response updateBdd(
            Map<String, Object> payload,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(payload);

        /* 🔐 Authorization handling (same framework pattern) */
        if ("MISSING".equalsIgnoreCase(authType)) {
            // No auth header
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req
                .when()
                .put("/api/updateBdd");
    }
}