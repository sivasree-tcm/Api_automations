package api.configuration;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class ProjectConfigApi {

    public static Response saveConfiguration(Object request, String role, String authType) {
        System.out.println("\n⚙️ ProjectConfigApi.saveConfiguration called");

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        // ===================== AUTH HANDLING =====================
        if (!"MISSING".equalsIgnoreCase(authType)) {
            String token = TokenUtil.getToken(tests.roles.UserRole.valueOf(role));
            if (token == null || token.isBlank()) {
                throw new IllegalStateException("❌ Auth token is missing. Please check login flow.");
            }
            // Backend expects RAW token in Authorization header
            req.header("Authorization", token);
        }

        // ===================== API CALL =====================
        return req
                .body(request)
                .log().all()
                .when()
                .post("/api/saveConfig")
                .then()
                .log().all()
                .extract()
                .response();
    }
}