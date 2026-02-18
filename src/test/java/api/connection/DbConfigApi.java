package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class DbConfigApi {

    public static Response addDbInfo(Object request, String role, String authType) {

        System.out.println("\n🔧 DbConfigApi.addDbInfo called");
        System.out.println("Role: " + role);
        System.out.println("Auth Type: " + authType);

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        // ===================== AUTH HANDLING =====================
        if (!"MISSING".equalsIgnoreCase(authType)) {

            String token;

            if ("INVALID".equalsIgnoreCase(authType)) {
                token = "invalid_token";
                System.out.println("⚠️ Using INVALID token for negative testing");
            } else {
                token = TokenUtil.getToken(
                        tests.roles.UserRole.valueOf(role)
                );
                System.out.println("🔑 Using token from TokenUtil");
            }

            if (token == null || token.isBlank()) {
                throw new IllegalStateException("❌ Token is NULL or EMPTY. Login flow is broken.");
            }

            // Token preview (safe)
            System.out.println("Token preview: " +
                    (token.length() > 30 ? token.substring(0, 30) + "..." : token));

            // ❗ IMPORTANT: Backend expects RAW token, NOT Bearer
            req.header("Authorization", token);

        } else {
            System.out.println("⚠️ No Authorization header (MISSING auth type)");
        }

        // ===================== API CALL =====================
        System.out.println("\n📤 Sending request to /api/addDbInfo");

        Response response = req
                .body(request)
                .log().all()
                .when()
                .post("/api/addDbInfo")
                .then()
                .log().all()
                .extract()
                .response();

        // ===================== RESPONSE LOG =====================
        System.out.println("\n📥 Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        return response;
    }
}
