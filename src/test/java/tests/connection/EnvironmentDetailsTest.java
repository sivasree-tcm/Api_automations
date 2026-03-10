package tests.connection;

import api.configuration.EnvironmentApi;
import base.BaseTest;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class EnvironmentDetailsTest extends BaseTest {

    public void fetchEnvironmentDetailsTest() {

        Report testData = JsonUtils.readJson(
                "testdata/connectionsData/getEnvironment.json",
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

            // ✅ ProjectId Injection
            Integer savedId = ProjectStore.getProjectId();
            int projectIdToUse = (savedId != null) ? savedId : -1;

            request.put("projectId", projectIdToUse);

            // ✅ Dynamic User ID
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", String.valueOf(actualUserId));

            // ✅ Store payload back to report
            tc.setRequest(request);

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