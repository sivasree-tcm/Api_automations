package api.ats;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class LoadATSFilesApi {

    public static Response loadATS(
            Map<String, Object> payload,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(payload);

        if ("VALID".equalsIgnoreCase(authType)) {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req.when().post("/api/loadAtsFiles");
    }
}