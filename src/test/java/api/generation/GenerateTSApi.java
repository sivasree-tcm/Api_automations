package api.generation;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GenerateTSApi {

    public static Response generateTS(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        // 🔐 Authorization
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no token
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req
                .body(request)
                .when()
                .post("/api/generateTS");
    }
}
