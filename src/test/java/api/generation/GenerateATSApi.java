package api.generation;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.*;

import java.util.Map;

public class GenerateATSApi {

    public static Response generateATS(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        // Auth handling
        if (!"MISSING".equalsIgnoreCase(authType)) {
            String token = TokenUtil.getToken(
                    tests.roles.UserRole.valueOf(role)
            );
            req.header("Authorization", token);
        }

        return req
                .body(request)
                .post("/api/generateATS");
    }
}
