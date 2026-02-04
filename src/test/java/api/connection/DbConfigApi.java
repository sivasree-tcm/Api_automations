package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;

public class DbConfigApi {

    public static Response addDbInfo(Object request, String role, String authType) {
        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        if (!"MISSING".equalsIgnoreCase(authType)) {
            String token = "INVALID".equalsIgnoreCase(authType) ? "invalid_token" :
                    TokenUtil.getToken(tests.roles.UserRole.valueOf(role));
            req.header("Authorization", "Bearer " + token);
        }

        return req.body(request)
                .when()
                .post("https://test.cognitest.ai/api/addDbInfo"); //
    }
}