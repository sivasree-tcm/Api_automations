package api.project;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import utils.TokenUtil;

public class GetMyProjectsApi {

    public static Response getMyProjects(
            Object request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if ("MISSING".equalsIgnoreCase(authType)) {
            // no Authorization header
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
                .post("/api/getMyProjects");
    }
}