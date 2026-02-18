package api.roles;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class createroleproject {

    public static Response createRole(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth header
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

        // ✅ CORRECT endpoint (NO /api)
        return req
                .body(request)
                .when()
                .post("/createRole");
    }
}
