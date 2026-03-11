package tests.files;

import api.files.DownloadFilesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;

public class DownloadFilesTest extends BaseTest {

    public void downloadSingleImageForBR() {
        // 1. Get Project and User IDs dynamically
        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        // 2. Get the list of BR IDs for this specific project
        List<Integer> brIds = BusinessRequirementStore.getBrIds(projectId);

        if (brIds == null || brIds.isEmpty()) {
            throw new RuntimeException("❌ No Business Requirements found for Project ID: " + projectId);
        }

        // 3. Pick the first available BR ID dynamically
        String dynamicBrId = String.valueOf(brIds.get(0));

        Report testData = JsonUtils.readJson(
                "testdata/uploadFiles/downloadFiles.json",
                Report.class
        );

        // Fix for NullPointerException in TestCase copy constructor
        if (testData.getTestCases().get(0).getRequest() == null) {
            testData.getTestCases().get(0).setRequest(new HashMap<>());
        }

        Report.TestCase tc = new Report.TestCase(testData.getTestCases().get(0));

        // 4. Construct the Payload using the dynamic BR ID
        Map<String, Object> downloadPayload = new HashMap<>();
        downloadPayload.put("fileName", projectId + "/" + dynamicBrId + "/sample.png");
        downloadPayload.put("userProjectId", String.valueOf(projectId));
        downloadPayload.put("userBrId", dynamicBrId);
        downloadPayload.put("storageType", "S3");
        downloadPayload.put("userId", String.valueOf(userId));

        tc.setRequest(downloadPayload);
        tc.setTcId("DOWNLOAD_FILE_BR_" + dynamicBrId);
        tc.setName("Download Image | BR " + dynamicBrId);

        // 5. Execute with the dynamic payload
        ApiTestExecutor.

                execute(
                "Download Files API",
                tc,
                () -> {
                    Response response = DownloadFilesApi.downloadFile(downloadPayload, tc.getRole());

                    if (response.getStatusCode() == 200) {
                        byte[] imageBytes = response.asByteArray();
                        Assert.assertTrue(imageBytes.length > 0, "❌ Downloaded file is empty!");
                        System.out.println("✅ Success! Downloaded " + imageBytes.length + " bytes for BR: " + dynamicBrId);
                    } else {
                        // Capture the HTML/JSON error from the server
                        System.out.println("❌ Error " + response.getStatusCode() + ": " + response.asString());
                    }
                    return response;
                }
        );
    }
}