package api.prompt;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetAllPromptApi {
    public static Response getAllPrompts(Object request, String role) {
        var req = given()
                .relaxedHTTPSValidation(); // standard for your environment

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request instanceof java.util.Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId != null) {
                req.queryParam("userId", userId);
            }
        }

        // Removed hardcoded baseUri; uses global config
        return req.get("/api/getAllPrompts");
    }
}