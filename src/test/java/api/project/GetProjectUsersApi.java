package api.project;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetProjectUsersApi {

    public static Response getProjectUsers(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        if ("MISSING".equalsIgnoreCase(authType)) {
            // no token
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(tests.roles.UserRole.valueOf(role))
            );
        }

        return req
                .body(request)
                .when()
                .post("/api/getProjectUsers");
    }
}
