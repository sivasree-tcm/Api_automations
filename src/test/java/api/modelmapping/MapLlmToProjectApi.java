package api.modelmapping;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class MapLlmToProjectApi {

    public static Response mapModelToProject(
            Object requestPayload,
            String role,
            String authType
    ) {

        System.out.println("\n🤖 MapLlmToProjectApi.mapModelToProject called");
        System.out.println("Role: " + role);
        System.out.println("Auth Type: " + authType);

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        if (!"MISSING".equalsIgnoreCase(authType)) {

            String token;

            if ("INVALID".equalsIgnoreCase(authType)) {
                token = "invalid_token";
                System.out.println("⚠️ Using INVALID token");
            } else {
                token = TokenUtil.getToken(
                        tests.roles.UserRole.valueOf(role)
                );
                System.out.println("🔑 Using token from TokenUtil");
            }

            if (token == null || token.isBlank()) {
                throw new IllegalStateException("❌ Token is null or empty");
            }

            req.header("Authorization", token);
        }

        System.out.println("\n📤 Sending request to /api/map-llm-to-project");

        Response response = req
                .body(requestPayload)
                .log().all()
                .when()
                .post("/api/map-llm-to-project")
                .then()
                .log().all()
                .extract()
                .response();

        System.out.println("\n📥 Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        return response;
    }
}
