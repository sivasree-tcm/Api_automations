package tests.connection; // Ensure this file is moved to src/test/java/tests/connection/

import api.connection.DbConfigApi;
import base.BaseTest;
import models.connection.DbConfigReport;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;
import java.util.Map;
import java.util.List;

public class DbConfigTest extends BaseTest {

    @Test
    public void addDbInfoTest() {
        // Updated path to match the reference style
        DbConfigReport testData = JsonUtils.readJson(
                "testdata/connectionsData/dbConfig.json",
                DbConfigReport.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(DbConfigReport testData, List<DbConfigReport.TestCase> cases) {
        for (DbConfigReport.TestCase tc : cases) {
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // ✅ Dynamic Injection - Logic from today's debugging
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", actualUserId);

            Integer savedId = ProjectStore.getProjectId();
            if (savedId != null) {
                request.put("projectId", savedId); // Injected Project ID: 1556
            }

            // ✅ Use the specific key found in your working payload
            // This matches "environemntDetails" (with the spelling from your confirmation)
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> DbConfigApi.addDbInfo(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}