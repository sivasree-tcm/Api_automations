package api.UserManagement;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class UserManagementApi {

    public static Response addOrUpdateProjectUser(
            Object request,
            String role
    ) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        // Authorization handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/addUpdateProjectUser");
    }
}
