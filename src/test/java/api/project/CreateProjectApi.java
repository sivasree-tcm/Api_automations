package api.project;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class CreateProjectApi {

    public static Response createProject(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // No Authorization header
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

        // Attach request body
        if (request != null) {
            req.body(request);
        }

        return req
                .when()
                .post("/api/createProject");
    }
}