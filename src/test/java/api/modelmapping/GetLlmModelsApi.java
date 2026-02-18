package api.modelmapping;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetLlmModelsApi {

    public static Response getModels(int userId, String role, String authType) {

        System.out.println("\n📥 GetLlmModelsApi.getModels called");

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .queryParam("userId", userId);

        if (!"MISSING".equalsIgnoreCase(authType)) {
            String token = TokenUtil.getToken(
                    tests.roles.UserRole.valueOf(role)
            );
            req.header("Authorization", token);
        }

        return req
                .log().all()
                .when()
                .get("/api/get-llm-models")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
