package tests.testCase;

import api.testCase.AddTestCaseStepApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class AddTestCaseStepTest extends BaseTest {

    public void addTestCaseStep() {

        // 🔁 HARD GUARD – Ensure TestCaseId exists
        if (!TestCaseStore.hasTestCases()) {
            TestCaseStore.log();
            throw new RuntimeException(
                    "❌ No TestCaseId available. Cannot add test steps."
            );
        }

        Integer testCaseId = TestCaseStore.getAnyTestCaseId();

        if (testCaseId == null) {
            TestCaseStore.log();
            throw new RuntimeException(
                    "❌ TestCaseStore present but TC ID is null."
            );
        }

        System.out.println("🧪 Adding steps for TestCaseId → " + testCaseId);

        // ✅ CRITICAL FIX – Prevent stale Step IDs
        TestStepStore.clear();

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/testCase/addTestCaseStep.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // ✅ Deterministic step generation
            String stepDescription =
                    TestStepGenerator.generateStepDescription(
                            "the most recent entry",
                            "in the audit trail shows the message communication"
                    );

            String expectedResult =
                    TestStepGenerator.generateExpectedResult(
                            "<<MESSAGE_SUBJECT>>"
                    );

            request.put("tcStepDescription", stepDescription);
            request.put("refTcId", testCaseId);
            request.put("tcStepExpectedResult", expectedResult);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                AddTestCaseStepApi.addTestCaseStep(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        // ✅ FIX – Extract correct identifier from API response
                        Object rawStepId = response.jsonPath().get("tcStepId");

                        if (rawStepId == null) {

                            System.out.println("❌ tcStepId missing in Add Step response");
                            System.out.println("Response → " + response.asPrettyString());

                            throw new RuntimeException(
                                    "Step creation failed. tcStepId not returned."
                            );
                        }

                        Integer stepId = Integer.valueOf(String.valueOf(rawStepId));

                        TestStepStore.add(stepId);

                        System.out.println(
                                "✅ Stored Newly Created StepId → " + stepId
                        );

                        return response;
                    }
            );
        }
    }
}
