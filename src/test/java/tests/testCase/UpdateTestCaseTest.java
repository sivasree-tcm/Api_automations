package tests.testCase;

import api.testCase.UpdateTestCaseApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class UpdateTestCaseTest extends BaseTest {


    public void updateTestCaseApiTest() {

        if (!TestCaseStore.hasTestCases()) {
            throw new RuntimeException(
                    "❌ No TestCaseId available. Run TC generation & fetch before update."
            );
        }

        Report testData =
                JsonUtils.readJson(
                        "testdata/testCase/updateTestCase.json",
                        Report.class
                );

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // ✅ Dynamic values
            request.put("testCaseId", TestCaseStore.getAnyTestCaseId());
            request.put("projectId", ProjectStore.getProjectId());
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            // ✅ Static update fields (business logic)
            request.put("tcDescription",
                    "Verify that administrators can create a new grantee profile with all required information");
            request.put("testCasePrecondition",
                    "User has administrator access to the grant management system");
            request.put("tcPriority", "high");
            request.put("regression", "Y");
            request.put("smoke", "Y");
            request.put("uat", "Y");
            request.put("testData", null);
            request.put("testCaseType", "positive");

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> UpdateTestCaseApi.updateTestCase(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
