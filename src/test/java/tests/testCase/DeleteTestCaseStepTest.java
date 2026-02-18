package tests.testCase;

import api.testCase.DeleteTestCaseStepApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DeleteTestCaseStepTest extends BaseTest {

    public void deleteTestCaseStep() {

        // 🔁 HARD GUARD – Ensure Step IDs exist
        if (!TestStepStore.hasSteps()) {
            TestStepStore.log();
            throw new RuntimeException(
                    "❌ No TestStepId available. Cannot delete test step."
            );
        }

        Integer stepId = TestStepStore.getAnyStepId();

        if (stepId == null) {
            TestStepStore.log();
            throw new RuntimeException(
                    "❌ TestStepStore present but Step ID is null."
            );
        }

        System.out.println("🧪 Deleting TestStepId → " + stepId);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/testCase/deleteTestCaseStep.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            request.put("tcStepId", java.util.List.of(stepId));
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                DeleteTestCaseStepApi.deleteTestCaseStep(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        String message =
                                response.jsonPath().getString("message");

                        if (message == null ||
                                !message.contains("deactivated")) {

                            throw new RuntimeException(
                                    "❌ Step deletion failed → " + stepId
                            );
                        }

                        System.out.println(
                                "✅ Step deleted successfully → " + stepId
                        );

                        return response;
                    }
            );
        }
    }
}
