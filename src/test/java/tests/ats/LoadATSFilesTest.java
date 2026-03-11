package tests.ats;

import api.ats.LoadATSFilesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class LoadATSFilesTest extends BaseTest {

    public void loadATSFiles() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ Project ID missing in ProjectStore.");
        }

        String projectName = ProjectStore.getProjectName(projectId);
        String framework = ProjectStore.getAutomationFramework();
        String storageType = ProjectStore.getStorageType();
        String userId = String.valueOf(TokenUtil.getUserId());
        String tcNumber = TestCaseStore.getAnyTestCaseNumber();

        if (projectName == null || framework == null ||
                storageType == null || tcNumber == null) {
            throw new RuntimeException("❌ Required store values missing.");
        }

        System.out.println("✅ Using Automation Framework → " + framework);

        Report testData = JsonUtils.readJson(
                "testdata/ats/loadATSFiles.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ loadATSFiles.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            executeForType(testData, tc, projectId, userId,
                    framework, projectName, tcNumber, storageType, "TestCase");

            executeForType(testData, tc, projectId, userId,
                    framework, projectName, tcNumber, storageType, "MenuPage");
        }
    }

    /* ========================================================= */

    private void executeForType(
            Report testData,
            Report.TestCase tc,
            Integer projectId,
            String userId,
            String framework,
            String projectName,
            String tcNumber,
            String storageType,
            String requestedType
    ) {

        // ✅ BUILD PAYLOAD FIRST (CRITICAL FIX)
        Map<String, Object> request = new HashMap<>();
        request.put("userProjectId", String.valueOf(projectId));
        request.put("userId", userId);
        request.put("automationFramework", framework);
        request.put("projectName", projectName);
        request.put("testCaseNumber", tcNumber);
        request.put("storageType", storageType);
        request.put("requestedType", requestedType);

        tc.setRequest(request);   // ⭐ NOW executor sees payload

        ApiTestExecutor.execute(
                testData.getScenario() + " | " + requestedType,
                tc,
                () -> {

                    System.out.println(
                            "📦 Load ATS Payload (" + requestedType + ") → " + request
                    );

                    Response response = LoadATSFilesApi.loadATS(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    );

                    if (response == null) {
                        throw new RuntimeException("❌ Load ATS API returned NULL response.");
                    }

                    if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                        throw new RuntimeException(
                                "❌ Load ATS Failed (" + requestedType + ") → Status: "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("✅ ATS Loaded Successfully → " + requestedType);

                    return response;
                }
        );
    }
}
