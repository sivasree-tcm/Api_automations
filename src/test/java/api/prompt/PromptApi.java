package api.prompt;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.prompt.CreatePromptTestData;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PromptApi {




        public static Response getAllPrompts(Object request, String role) {

            var req = given()
                    .baseUri("https://test.cognitest.ai");

            // Authorization
            if (!"NO_AUTH".equalsIgnoreCase(role)) {
                req.header("Authorization", TokenUtil.getToken(role));
            }

            // Extract userId from payload-like request
            if (request instanceof java.util.Map<?, ?> map) {
                Object userId = map.get("userId");
                if (userId != null) {
                    req.queryParam("userId", userId);
                }
            }

            // MUST BE GET
            return req.get("/api/getAllPrompts");
        }


    public static Response createPrompt(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/createPrompt");
    }
    public static Response updatePrompt(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/updatePrompt");
    }

    public static Response mapPrompt(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/mapPrompt");
    }


}
