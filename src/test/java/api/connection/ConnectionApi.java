package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ConnectionApi {

    public static Response getConnections(Object request, String role) {

        var req = given()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken());

        if (request != null) {
            req.body(request);
        }

        return req
                .when()
                .post("/api/getConnections");
    }
}