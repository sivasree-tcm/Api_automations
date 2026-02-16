package api.export;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ExportTCExcelApi {

    public static Response downloadTCExcel(
            List<Integer> testScenarioIds,
            Integer userId,
            String role,
            String authType
    ) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("testScenarioId", testScenarioIds);
        payload.put("userId", userId);

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(payload);

        if ("VALID".equalsIgnoreCase(authType)) {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req
                .when()
                .post("/api/getTestCaseWithStepsExcel");
    }
}
