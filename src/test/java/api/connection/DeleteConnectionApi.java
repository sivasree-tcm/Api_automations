package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class DeleteConnectionApi {

    public static Response deleteConnection(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no token
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        }
        else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .body(request)
                .when()
                .post("/api/deleteConnection");
    }
}
