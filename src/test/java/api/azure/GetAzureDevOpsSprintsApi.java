package api.azure;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetAzureDevOpsSprintsApi {

    public static Response getAzureDevOpsSprints(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json");

        if ("MISSING".equalsIgnoreCase(authType)) {
            // ❌ No Authorization header
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
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
                .body(request)
                .post("/api/getAzureDevOpsSprints");
    }
}