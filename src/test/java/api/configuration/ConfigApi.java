package api.configuration;

import io.restassured.response.Response;
import models.configuration.ConfigRequest;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ConfigApi {

    public static Response getConfig(ConfigRequest request) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TokenUtil.getToken())
                .body(request)
                .when()
                .post("/api/getConfig");
    }

    public static Response saveConfig(ConfigRequest request) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TokenUtil.getToken())
                .body(request)
                .when()
                .post("/api/saveConfig");
    }

    public static Response addDbInfo(ConfigRequest request) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + TokenUtil.getToken())
                .body(request)
                .when()
                .post("/api/addDbInfo");
    }
}
