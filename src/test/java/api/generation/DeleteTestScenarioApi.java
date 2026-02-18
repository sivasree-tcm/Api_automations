package api.generation;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class DeleteTestScenarioApi {

    public static Response deleteTestScenario(
            Object request,
            String role
    ) {
        return given()
                .contentType("application/json")
                .header(
                        "Authorization",
                        TokenUtil.getToken(
                                tests.roles.UserRole.valueOf(role)
                        )
                )
                .body(request)
                .when()
                .put("/api/deleteTestScenario");
    }
}
