package tests.project;

import api.project.GetTestCaseWithStepsApi;
import base.BaseTest;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTestCaseWithStepsTest extends BaseTest {

    public void getTestCaseWithSteps() {

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
                    () -> {

                        Response response =
                                GetTestCaseWithStepsApi.getTestCaseWithSteps(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        // ✅ Extract Steps
                        List<Map<String, Object>> steps =
                                response.jsonPath().getList("steps");

                        if (steps == null || steps.isEmpty()) {
                            throw new RuntimeException(
                                    "❌ No test steps returned for TC " + testCaseId
                            );
                        }

                        for (Map<String, Object> step : steps) {

                            if (step == null) {
                                System.out.println("⚠ Skipping NULL step entry from response");
                                continue;
                            }

                            Object rawStepId = step.get("tcStepId");   // ✅ FIXED

                            if (rawStepId == null) {
                                System.out.println("⚠ Step entry without tcStepId → " + step);
                                continue;
                            }

                            Integer stepId = Integer.valueOf(String.valueOf(rawStepId));

                            TestStepStore.add(stepId);
                        }

                        System.out.println(
                                "📦 Stored Step IDs for TC " +
                                        testCaseId + " → " + steps.size()
                        );

                        return response;
                    }
            );
        }
    }
}
