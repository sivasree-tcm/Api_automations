package api.generation;

import io.restassured.response.Response;
import utils.TokenUtil;
import static io.restassured.RestAssured.given;
import java.util.Map;

public class GenerateTCApi {

    public static Response generateTC(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if (!"MISSING".equalsIgnoreCase(authType)) {
            req.header("Authorization",
                    TokenUtil.getToken(tests.roles.UserRole.valueOf(role)));
        }

        return req
                .body(request)
                .post("/api/generateTC");
    }
}