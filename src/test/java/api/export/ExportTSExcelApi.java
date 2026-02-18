package api.export;

import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ExportTSExcelApi {

    public static Response downloadTSExcel(
            Integer brId,
            Integer userId,
            String role,
            String authType
    ) {

        var req = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(
                        Map.of(
                                "brId", brId.toString(),
                                "userId", userId
                        )
                );

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
                .post("/api/getTSExcel");
    }
}
