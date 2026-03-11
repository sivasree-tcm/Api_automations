package tests.configuration;

import api.configuration.SaveConfigApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class SaveConfigTest extends BaseTest {

    public void saveConfigTest() {

        Report testData = JsonUtils.readJson(
                "testdata/configuration/saveConfig.json",
                Report.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(Report testData, List<Report.TestCase> cases) {

        for (Report.TestCase tc : cases) {

            Map<String, Object> request;

            if (tc.getRequest() != null) {
                request = new HashMap<>((Map<String, Object>) tc.getRequest());
            } else {
                request = new HashMap<>();
            }

            // ✅ Dynamic UserId Injection
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", String.valueOf(actualUserId));

            // ✅ Dynamic ProjectId Injection
            Integer savedProjectId = ProjectStore.getProjectId();

            if (savedProjectId != null) {
                request.put("projectId", String.valueOf(savedProjectId));
                System.out.println("🔗 Injected Project ID: " + savedProjectId);
            } else {
                System.err.println("⚠️ Warning: ProjectStore is empty. Using default/JSON ID.");
            }

            // ✅ Store payload back to report (important for report logging)
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> SaveConfigApi.saveConfiguration(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}