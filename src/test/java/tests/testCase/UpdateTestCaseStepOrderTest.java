package tests.testCase;

import api.testCase.UpdateTestCaseStepOrderApi;
import api.testCase.GetTestCaseWithStepsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.TestCaseStore;
import utils.TokenUtil;

import java.util.*;

public class UpdateTestCaseStepOrderTest extends BaseTest {

    public void updateTestCaseStepOrderApiTest() {

        // Read JSON Test Data
        Report testData = JsonUtils.readJson(
                "testdata/testCase/updateTestCaseStepOrder.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ updateTestCaseStepOrder.json missing or invalid.");
        }

        // Get all generated Test Case IDs
        List<Integer> allTestCases = TestCaseStore.getAll();
        if (allTestCases == null || allTestCases.isEmpty()) {
            throw new RuntimeException("❌ No TestCases found in TestCaseStore. Run generation first.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // Resolve userId dynamically
                        String userId = String.valueOf(TokenUtil.getUserId(tc.getRole()));

                        Integer selectedTcId = null;
                        List<Map<String, Object>> stepsToReorder = null;

                        // Find a test case having >=2 steps
                        for (Integer tcId : allTestCases) {

                            Map<String, Object> fetchRequest = new HashMap<>();
                            fetchRequest.put("testCaseId", tcId);
                            fetchRequest.put("userId", userId);

                            Response response = GetTestCaseWithStepsApi.getTestCaseWithSteps(
                                    fetchRequest,
                                    tc.getRole(),
                                    "VALID"
                            );

                            if (response.getStatusCode() != 200) {
                                continue;
                            }

                            List<Map<String, Object>> fetchedSteps =
                                    response.jsonPath().getList("steps") != null
                                            ? response.jsonPath().getList("steps")
                                            : response.jsonPath().getList("$");

                            if (fetchedSteps == null || fetchedSteps.isEmpty()) {
                                continue;
                            }

                            // Filter only valid step maps
                            List<Map<String, Object>> validSteps = new ArrayList<>();
                            for (Object obj : fetchedSteps) {
                                if (obj instanceof Map) {
                                    validSteps.add((Map<String, Object>) obj);
                                }
                            }

                            if (validSteps.size() >= 2) {
                                selectedTcId = tcId;
                                stepsToReorder = validSteps;
                                break;
                            }
                        }

                        if (selectedTcId == null || stepsToReorder == null) {
                            throw new RuntimeException("❌ No TestCase found with >= 2 valid steps.");
                        }

                        // Reverse step order
                        Collections.reverse(stepsToReorder);

                        List<Map<String, Object>> updatedStepsPayload = new ArrayList<>();

                        int counter = 1;

                        for (Map<String, Object> step : stepsToReorder) {

                            if (step == null) {
                                continue;
                            }

                            Object stepId = step.get("tcStepId") != null
                                    ? step.get("tcStepId")
                                    : step.get("id");

                            if (stepId == null) {
                                continue;
                            }

                            Map<String, Object> stepData = new HashMap<>();
                            stepData.put("tcStepId", stepId);
                            stepData.put("sortOrder", counter);
                            stepData.put("newStepNumber",
                                    String.format("TCS-%d-%03d", selectedTcId, counter)
                            );

                            updatedStepsPayload.add(stepData);
                            counter++;
                        }

                        if (updatedStepsPayload.isEmpty()) {
                            throw new RuntimeException("❌ No valid steps found for reorder.");
                        }

                        // Final payload
                        Map<String, Object> finalPayload = new HashMap<>();
                        finalPayload.put("steps", updatedStepsPayload);
                        finalPayload.put("userId", userId);

                        // Debug log (useful for report debugging)
                        System.out.println("FINAL STEP ORDER PAYLOAD → " + finalPayload);

                        return UpdateTestCaseStepOrderApi.updateStepOrder(
                                finalPayload,
                                tc.getRole(),
                                tc.getAuthType()
                        );
                    }
            );
        }
    }
}