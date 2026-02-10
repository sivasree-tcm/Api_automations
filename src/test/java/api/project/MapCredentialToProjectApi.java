package api.project;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class MapCredentialToProjectApi {

    public static Response mapCredential(
            Object request,
            String role,
            String authType
    ) {

        Map<String, Object> reqMap =
                (Map<String, Object>) request;

        var req = given()
                .contentType("application/json")
                .body(reqMap);

        // 🔐 Auth handling (standard)
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
                .post("/api/map-credentials-to-project");
    }
}
