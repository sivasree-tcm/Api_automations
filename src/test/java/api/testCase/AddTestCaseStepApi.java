package api.testCase;

import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AddTestCaseStepApi {

    public static Response addTestCaseStep(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json")
                .body(request);

        if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .when()
                .post("/api/addTestCaseStep");
    }
}