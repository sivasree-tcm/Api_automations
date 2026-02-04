package tests.connection;

import api.connection.DbConfigApi;
import base.BaseTest;
import models.connection.DbConfigReport;
import org.testng.annotations.Test;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;
import java.util.Map;
import java.util.List;

public class DbConfigTest extends BaseTest {

    @Test
    public void addDbInfoTest() {
        DbConfigReport testData = JsonUtils.readJson(
                "testdata/connectionsData/dbConfig.json",
                DbConfigReport.class
        );

        if (testData != null && testData.getTestCases() != null) {
            execute(testData, testData.getTestCases());
        } else {
            System.err.println("ERROR: Could not load dbConfig.json test data.");
        }
    }

    private void execute(DbConfigReport testData, List<DbConfigReport.TestCase> cases) {
        for (DbConfigReport.TestCase tc : cases) {
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // 🔹 1. Fix UserID Injection
            // Converting to Integer to match the format of successful requests
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", actualUserId);

            // 🔹 2. Fix ProjectID Injection
            // Retrieve the ID captured from the 'project_id' field in Step 2
            Integer savedId = ProjectStore.getProjectId();
            if (savedId != null) {
                request.put("projectId", savedId);
                System.out.println("DEBUG: Injected Project ID: " + savedId);
            } else {
                // Warning if the previous step failed to store the ID
                System.err.println("CRITICAL: ProjectID is null. Ensure createprojectflow passed and saved the project_id.");
            }

            // 🔹 3. API Execution
            // Ensure tc.getAuthType() is "VALID" in your JSON to provide the correct token
            tests.user.ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> DbConfigApi.addDbInfo(request, tc.getRole(), tc.getAuthType())
            );
        }
    }
}