package api.connection;

import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ConnectionApi {

    public static Response getConnections(Object requestBody) {

        return given()
                .header("Authorization", TokenUtil.getToken())
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/getConnections")
                .then()
                .extract()
                .response();
    }
}
