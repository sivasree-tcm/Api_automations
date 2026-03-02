package tests.connection;

import api.connection.EnvironmentApi;
import base.BaseTest;
import models.connection.DbConfigReport; // Reusing your existing model structure
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;
import java.util.Map;
import java.util.List;

public class EnvironmentDetailsTest extends BaseTest {

    public void fetchEnvironmentDetailsTest() {
        // Pointing to your environment JSON data
        DbConfigReport testData = JsonUtils.readJson(
                "testdata/connectionsData/getEnvironment.json",
                DbConfigReport.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(DbConfigReport testData, List<DbConfigReport.TestCase> cases) {
        for (DbConfigReport.TestCase tc : cases) {
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // ✅ Dynamic Injection from ProjectStore
            Integer savedId = ProjectStore.getProjectId();

            // If the store is empty, we use -1 as per your requirement
            int projectIdToUse = (savedId != null) ? savedId : -1;
            request.put("projectId", projectIdToUse);

            // ✅ Dynamic User ID injection
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", String.valueOf(actualUserId));

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> EnvironmentApi.getEnvironmentDetails(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}