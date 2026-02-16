package api.br;

import io.restassured.response.Response;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.MultiPartConfig;

import java.io.File;

import static io.restassured.RestAssured.given;

public class UploadBusinessRequirementApi {

    public static Response uploadBR(
            File file,
            int userId,
            int projectId,
            String token
    ) {

        if (!file.exists()) {
            throw new IllegalStateException("❌ File not found: " + file.getAbsolutePath());
        }

        return given()
                // 🔐 Auth
                .header("Authorization", token)

                // ✅ Let RestAssured handle multipart config automatically
                .config(RestAssuredConfig.config()
                        .multiPartConfig(MultiPartConfig.multiPartConfig()
                                .defaultBoundary("----WebKitFormBoundary7MA4YWxkTrZu0gW")))

                // ✅ File upload with correct control name and content type
                .multiPart("excelFile", file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

                // ✅ Query params
                .queryParam("userId", userId)
                .queryParam("projectId", projectId)

                .log().all()

                .when()
                .post("/api/uploadBusinessRequirement")

                .then()
                .log().all()
                .extract()
                .response();
    }
}