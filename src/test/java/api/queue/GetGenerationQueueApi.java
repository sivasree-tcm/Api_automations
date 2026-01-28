package api.queue;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class GetGenerationQueueApi {

    public static Response getGenerationQueue(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON);

        if (!"MISSING".equalsIgnoreCase(authType)) {
            req.header("Authorization",
                    TokenUtil.getToken(tests.roles.UserRole.valueOf(role)));
        }

        return req
                .body(request)
                .post("/api/getGenerationQueue");
    }
}
