package api.ats;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import tests.roles.UserRole;
import utils.TokenUtil;

import java.util.Map;

public class LoadATSFilesApi {

    public static Response loadATSFiles(
            Map<String, Object> request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType("application/json");

        // 🔐 Auth handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth header
        }
        else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        }
        else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        return req
                .body(request)
                .post("/api/loadAtsFiles");
    }
}