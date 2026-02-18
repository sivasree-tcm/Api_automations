package api.prompt;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class PromptApi {

    public static Response getAllPrompts(Object request, String role) {
        var req = given()
                .baseUri("https://test.tsigma.ai");

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request instanceof java.util.Map<?, ?> map) {
            Object userId = map.get("userId");
            if (userId != null) {
                req.queryParam("userId", userId);
            }
        }

        return req.get("/api/getAllPrompts");
    }

    public static Response createPrompt(Object request, String role) {
        return given()
                .baseUri("https://test.tsigma.ai")
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(role))
                .body(request)
                .log().all() // 👈 Logs the JSON you are sending
                .post("/api/createPrompt")
                .then()
                .log().all() // 👈 Logs the error message from the server
                .extract()
                .response();
    }

    public static Response updatePrompt(Object request, String role) {
        return given()
                .baseUri("https://test.tsigma.ai")
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(role))
                .body(request)
                .log().all()
                .post("/api/updatePrompt")
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static Response mapPrompt(Object request, String role) {
        return given()
                .contentType(ContentType.JSON)
                .header("Connection", "close")
                .baseUri("https://test.tsigma.ai")
                .header("Authorization", TokenUtil.getToken(role))
                .body(request)
                .log().all()
                .post("/api/mapPrompt")
                .then()
                .log().all()
                .extract()
                .response();
    }
}