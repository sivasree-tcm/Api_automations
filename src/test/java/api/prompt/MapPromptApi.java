package api.prompt;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import tests.roles.UserRole; // Assuming this import path matches your project structure

import static io.restassured.RestAssured.given;

public class MapPromptApi {

    public static Response mapPrompt(Object request, String role, String authType) {
        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .header("Connection", "close")
                .body(request);

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // No Authorization header applied
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header("Authorization", TokenUtil.getToken(UserRole.valueOf(role)));
        }

        return req
                .log().all()
                .post("/api/mapPrompt")
                .then()
                .log().all()
                .extract()
                .response();
    }
}