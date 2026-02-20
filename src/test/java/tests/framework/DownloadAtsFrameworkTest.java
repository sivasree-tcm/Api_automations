package tests.framework;

import api.framework.DownloadAtsFrameworkApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DownloadAtsFrameworkTest extends BaseTest {

    public void downloadAtsFramework() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        String projectName = ProjectStore.getProjectName(projectId);

        // ✅ Hard-coded for testing
        String automationFramework = "Playwright_Java";

        String storageType = ProjectStore.getStorageType();

        if (projectName == null || storageType == null) {
            throw new RuntimeException("❌ Missing framework configuration in ProjectStore");
        }

        System.out.println("🚀 Using Hard-coded Automation Framework → " + automationFramework);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/framework/downloadAtsFramework.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ downloadAtsFramework.json missing or invalid.");
        }

        for (ConnectionReport.TestCase baseTc : testData.getTestCases()) {

            // ✅ CRITICAL FIX → Clone TestCase (matches polling test behaviour)
            ConnectionReport.TestCase tc = new ConnectionReport.TestCase(baseTc);

            // ✅ Build payload BEFORE executor (report-safe)
            Map<String, Object> request = new HashMap<>();
            request.put("projectId", String.valueOf(projectId));
            request.put("userId", userId);
            request.put("automationFramework", automationFramework);
            request.put("projectName", projectName);
            request.put("storageType", storageType);

            tc.setRequest(request);

            tc.setTcId("DOWNLOAD_FRAMEWORK_" + projectId + "_" + automationFramework);
            tc.setName("Download ATS Framework | Project " +
                    projectId + " | " + automationFramework);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                DownloadAtsFrameworkApi.downloadFramework(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ Framework API returned null response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Framework download failed → " + automationFramework +
                                            " | Status → " + response.getStatusCode()
                            );
                        }

                        System.out.println("✅ Framework download successful → " + automationFramework);

                        return response;
                    }
            );
        }
    }
}
