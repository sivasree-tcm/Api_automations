package tests.connection;

import api.connection.ProjectConfigApi;
import base.BaseTest;
import models.connection.DbConfigReport; // Assuming common model for test case structure
import tests.user.ApiTestExecutor;
import utils.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class SaveProjectConfigTest extends BaseTest {

    public void saveConfigTest() {
        DbConfigReport testData = JsonUtils.readJson(
                "testdata/project/saveConfig.json",
                DbConfigReport.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(DbConfigReport testData, List<DbConfigReport.TestCase> cases) {
        for (DbConfigReport.TestCase tc : cases) {
            // Start with the base map from your JSON template
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // ✅ DYNAMIC INJECTION: User ID from TokenUtil
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", String.valueOf(actualUserId));

            // ✅ DYNAMIC INJECTION: Project ID from ProjectStore
            Integer savedProjectId = ProjectStore.getProjectId();
            if (savedProjectId != null) {
                request.put("projectId", String.valueOf(savedProjectId));
                System.out.println("🔗 Injected Project ID: " + savedProjectId);
            } else {
                System.err.println("⚠️ Warning: ProjectStore is empty. Using default/JSON ID.");
            }

            // Execute using your custom framework executor
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectConfigApi.saveConfiguration(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}