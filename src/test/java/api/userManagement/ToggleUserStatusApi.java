package api.userManagement;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ToggleUserStatusApi {
    public static Response toggleUserStatus(Object request, String role) {
        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/toggleUserStatus");
    }
}