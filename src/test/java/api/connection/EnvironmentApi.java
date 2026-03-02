package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class EnvironmentApi {

    public static Response getEnvironmentDetails(Object request, String role, String authType) {

        System.out.println("\n🌍 EnvironmentApi.getEnvironmentDetails called");

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        // ===================== AUTH HANDLING =====================
        if (!"MISSING".equalsIgnoreCase(authType)) {
            String token = TokenUtil.getToken(tests.roles.UserRole.valueOf(role));

            if (token == null || token.isBlank()) {
                throw new IllegalStateException("❌ Token is NULL. Login flow failed.");
            }

            // Following your backend requirement: RAW token, NOT Bearer
            req.header("Authorization", token);
        }

        // ===================== API CALL =====================
        return req
                .body(request)
                .log().all()
                .when()
                .post("/api/getEnvironmentDetails")
                .then()
                .log().all()
                .extract()
                .response();
    }
}