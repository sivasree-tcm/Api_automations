package api.files;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class DeleteFilesApi {

    public static Response deleteFile(Map<String, Object> payload, String role) {
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(tests.roles.UserRole.valueOf(role)))
                .body(payload)
                .post("/api/deleteFile"); // Based on your request URL
    }
}