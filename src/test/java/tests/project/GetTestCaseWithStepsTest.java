package tests.project;

import api.project.GetTestCaseWithStepsApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class GetTestCaseWithStepsTest extends BaseTest {

    @Test
    public void getTestCaseWithStepsApiTest() {

        // 🔁 HARD GUARD WITH LOGGING
        if (!TestCaseStore.hasTestCases()) {
            TestCaseStore.log();
            throw new RuntimeException(
                    "❌ No TestCaseId available. " +
                            "TC generation did not populate TestCaseStore."
            );
        }

        Integer testCaseId = TestCaseStore.getAnyTestCaseId();

        if (testCaseId == null) {
            TestCaseStore.log();
            throw new RuntimeException(
                    "❌ TestCaseStore is present but no TC ID found."
            );
        }

        System.out.println("🧪 Using TestCaseId → " + testCaseId);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getTestCaseWithSteps.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // ✅ Dynamic values
            request.put("testCaseId", testCaseId);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> GetTestCaseWithStepsApi.getTestCaseWithSteps(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
