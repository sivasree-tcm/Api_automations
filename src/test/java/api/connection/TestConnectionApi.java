package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.login.LoginTest;
import utils.TokenUtil;
import tests.roles.UserRole;

import static io.restassured.RestAssured.given;

public class TestConnectionApi {

    public static Response testConnection(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // No token
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token_123");
        }
        else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role))
            );
        }

        if (request != null) {
            req.body(request);
        }

        return req
                .when()
                .post("/api/testConnection");
    }
}
