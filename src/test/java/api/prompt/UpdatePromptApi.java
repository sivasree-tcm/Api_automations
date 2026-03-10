package api.prompt;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import utils.TokenUtil;

public class UpdatePromptApi {

    public static Response updatePrompt(
            Object request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if ("MISSING".equalsIgnoreCase(authType)) {

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

        return req
                .body(request)
                .post("/api/updatePrompt");
    }
}