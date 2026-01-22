package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class SaveConnectionApi {

    public static Response saveConnection(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Auth handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // No token
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token_123");
        }
        else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        if (request != null) {
            req.body(request);
        }

        return req
                .when()
                .post("/api/saveConnection");
    }
}
