package tests.connection; // Ensure this file is moved to src/test/java/tests/connection/

import api.configuration.DbConfigApi;
import base.BaseTest;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class DbConfigTest extends BaseTest {

    public void addDbInfoTest() {

        Report testData = JsonUtils.readJson(
                "testdata/configuration/dbConfig.json",
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

            // ✅ Dynamic User Injection
            int actualUserId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", actualUserId);

            // ✅ Dynamic Project Injection
            Integer savedId = ProjectStore.getProjectId();
            if (savedId != null) {
                request.put("projectId", savedId);
            }

            // ✅ Store payload back to report
            tc.setRequest(request);

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