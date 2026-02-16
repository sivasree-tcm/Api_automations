package tests.uploadFiles;

import api.uploadFiles.UploadFilesForBRApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFilesForBRTest extends BaseTest {

    public void uploadImageForALLBRs() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        List<Integer> brIds = BusinessRequirementStore.getBrIds(projectId);

        if (brIds == null || brIds.isEmpty()) {
            throw new RuntimeException("❌ No BRs found for image upload");
        }

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/uploadFiles/uploadFilesForBR.json",
                        ConnectionReport.class
                );

        // 🔥 MUST be done BEFORE copying TestCase
        testData.getTestCases().get(0).setRequest(new HashMap<>());

        File imageFile = new File("src/test/resources/files/sample.png");

        System.out.println("📁 Uploading file: " + imageFile.getAbsolutePath());

        if (!imageFile.exists()) {
            throw new RuntimeException(
                    "❌ Image file not found: " + imageFile.getAbsolutePath()
            );
        }

        for (Integer brId : brIds) {

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            // ✅ ONLY metadata for report (NO files here)
            Map<String, Object> reportRequest = new HashMap<>();
            reportRequest.put("brId", brId);
            reportRequest.put("projectId", projectId);
            reportRequest.put("userId", userId);
            reportRequest.put("storageType", "S3");

            tc.setRequest(reportRequest);
            tc.setTcId("UPLOAD_FILE_BR_" + brId);
            tc.setName("Upload Image | BR " + brId);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ REAL multipart request
                        Response response =
                                UploadFilesForBRApi.uploadFile(
                                        imageFile,
                                        brId,
                                        projectId,
                                        userId,
                                        "S3",
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        System.out.println("✅ Image uploaded for BR " + brId);
                        return response;
                    }
            );
        }
    }
}
