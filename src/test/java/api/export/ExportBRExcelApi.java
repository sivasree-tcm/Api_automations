package api.export;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ExportBRExcelApi {

    public static Response downloadBRExcel(
            Integer projectId,
            Integer userId,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(
                        Map.of(
                                "projectId", projectId.toString(),
                                "userId", userId
                        )
                );

        // 🔐 Authorization handling
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req
                .when()
                .post("/api/getBRforProjectIdExcel");
    }
}