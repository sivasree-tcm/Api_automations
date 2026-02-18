package api.project;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetProjectProviderConfigApi {

    public static Response getProjectProviderConfig(
            Object request,
            String role,
            String authType
    ) {

        Map<String, Object> reqMap = (Map<String, Object>) request;
        int userId = ((Number) reqMap.get("userId")).intValue();

        var req = given()
                .contentType("application/json")
                .queryParam("userId", userId);

        // 🔐 Auth handling (STANDARDIZED)
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

        return req
                .when()
                .post("/api/get-project-provider-config");
    }
}
