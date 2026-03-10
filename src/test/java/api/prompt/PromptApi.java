package api.prompt;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class PromptApi {

    public static Response createPrompt(Object request, String role) {
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(role))
                .body(request)
                .log().all()
                .post("/api/createPrompt")
                .then()
                .log().all()
                .extract()
                .response();
    }


    public static Response mapPrompt(Object request, String role) {
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .header("Connection", "close")
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