package api.azure;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import utils.TokenUtil;
import tests.roles.UserRole;

public class GetUserStoriesApi {

    public static Response getUserStories(
            Object request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth header
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
                .post("/api/getUserStories");
    }
}