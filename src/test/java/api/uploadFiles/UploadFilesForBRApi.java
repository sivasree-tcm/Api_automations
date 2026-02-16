package api.uploadFiles;

import io.restassured.response.Response;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.MultiPartConfig;
import utils.TokenUtil;

import java.io.File;

import static io.restassured.RestAssured.given;

public class UploadFilesForBRApi {

    public static Response uploadFile(
            File imageFile,
            Integer brId,
            Integer projectId,
            Integer userId,
            String storageType,
            String role,
            String authType
    ) {
        var req = given()
                .relaxedHTTPSValidation()
                .multiPart("files", imageFile, "image/png") // ✅ REAL FILE
                .formParam("brId", brId)
                .formParam("projectId", projectId)
                .formParam("userId", userId)
                .formParam("storageType", storageType);

        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header("Authorization", TokenUtil.getToken(
                    tests.roles.UserRole.valueOf(role)
            ));
        }

        return req.post("/api/upload-files");
    }

}
