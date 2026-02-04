package api.roles;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class DisableUsersByRoleApi {

    public static Response disableUsers(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        if ("MISSING".equalsIgnoreCase(authType)) {
            // No auth
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        return req
                .body(request)
                .post("/disableUsersByRole");
    }
}
