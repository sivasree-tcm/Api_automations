package api.userManagement;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class AddUserApi {

    public static Response addOrUpdateProjectUser(
            Object request,
            String role
    ) {
        var req = given()
                .relaxedHTTPSValidation() // Added for consistency with your other APIs
                .contentType(ContentType.JSON);

        // Authorization handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        // Removed hardcoded baseUri; uses global config now
        return req.post("/addUpdateProjectUser");
    }
}