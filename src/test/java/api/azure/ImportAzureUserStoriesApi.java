package api.azure;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import utils.TokenUtil;
import tests.roles.UserRole;

public class ImportAzureUserStoriesApi {

    public static Response importStories(
            Object request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if (!"MISSING".equalsIgnoreCase(authType)) {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .body(request)
                .post("/api/importAzureUserStories");
    }
}