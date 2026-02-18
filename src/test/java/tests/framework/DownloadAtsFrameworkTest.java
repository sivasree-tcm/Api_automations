package tests.framework;

import api.framework.DownloadAtsFrameworkApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

public class DownloadAtsFrameworkTest extends BaseTest {

    public void downloadAtsFramework() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        String projectName = ProjectStore.getProjectName(projectId);
        String automationFramework = ProjectStore.getAutomationFramework();
        String storageType = ProjectStore.getStorageType();

        if (projectName == null || automationFramework == null || storageType == null) {
            throw new RuntimeException("❌ Missing framework configuration in ProjectStore");
        }

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/framework/downloadAtsFramework.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                DownloadAtsFrameworkApi.downloadFramework(
                                        projectId,
                                        userId,
                                        automationFramework,
                                        projectName,
                                        storageType,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Framework download failed → " + automationFramework
                            );
                        }

                        System.out.println("✅ Framework download successful → " + automationFramework);

                        return response;
                    }
            );
        }
    }
}
