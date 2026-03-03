package api.userManagement;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetUserManagementDetailsApi {

    public static Response getUserManagementDetails(
            Map<String, Object> payload,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(payload);

        // 🔐 Authorization Handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no header
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
                .post("/api/getUserManagementDetailsForProjectId");
    }
}