package tests.testCase;

import api.testCase.UpdateTestCaseStepApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class UpdateTestCaseStepTest extends BaseTest {

    public void updateTestCaseStep() {

        // 🔁 HARD GUARD – Ensure Step IDs exist
        if (!TestStepStore.hasSteps()) {
            TestStepStore.log();
            throw new RuntimeException(
                    "❌ No TestStepId available. Cannot update test step."
            );
        }

        Integer testStepId = TestStepStore.getAnyStepId();

        if (testStepId == null) {
            TestStepStore.log();
            throw new RuntimeException(
                    "❌ TestStepStore present but Step ID is null."
            );
        }

        System.out.println("🧪 Updating TestStepId → " + testStepId);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/testCase/updateTestCaseStep.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // ✅ Regenerated deterministic step content
            String updatedDescription =
                    TestStepGenerator.generateStepDescription(
                            "the updated audit entry",
                            "reflects the modified step description"
                    );

            String updatedExpectedResult =
                    TestStepGenerator.generateExpectedResult(
                            "<<UPDATED_MESSAGE_SUBJECT>>"
                    );

            request.put("tcStepId", testStepId);
            request.put("tcStepDescription", updatedDescription);
            request.put("tcStepExpectedResult", updatedExpectedResult);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                UpdateTestCaseStepApi.updateTestCaseStep(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        System.out.println(
                                "✅ Step updated successfully → " + testStepId
                        );

                        return response;
                    }
            );
        }
    }
}