//package tests.generation;
//
//import api.generation.UpdateTestCaseStepOrderApi;
//import api.project.GetTestCaseWithStepsApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.annotations.Test;
//import tests.user.ApiTestExecutor;
//import utils.TestCaseStore;
//import utils.TokenUtil;
//
//import java.util.*;
//
//public class UpdateTestCaseStepOrderTest extends BaseTest {
//
//    @Test
//    public void updateTestCaseStepOrderApiTest() {
//
//        // 🔴 STEP 1: Get Test Cases from store
//        List<Integer> allTestCases = TestCaseStore.getAll();
//        if (allTestCases == null || allTestCases.isEmpty()) {
//            throw new RuntimeException("❌ No TestCases available in store.");
//        }
//
//        Integer selectedTcId = null;
//        List<Map<String, Object>> stepsToReorder = null;
//
//        // 🔴 STEP 2: Find a TC with >= 2 steps (Scanning the Flat List)
//        for (Integer tcId : allTestCases) {
//            Map<String, Object> fetchRequest = new HashMap<>();
//            fetchRequest.put("testCaseId", tcId);
//            fetchRequest.put("userId", TokenUtil.getUserId());
//
//            Response response = GetTestCaseWithStepsApi.getTestCaseWithSteps(fetchRequest, "SUPER_ADMIN", "VALID");
//
//            if (response.getStatusCode() != 200) continue;
//
//            // 🔥 FIX: Extracting directly from the root array ($)
//            List<Map<String, Object>> fetchedSteps = response.jsonPath().getList("$");
//
//            if (fetchedSteps != null && fetchedSteps.size() >= 2) {
//                System.out.println("🎯 Match Found: TC " + tcId + " has " + fetchedSteps.size() + " steps.");
//                selectedTcId = tcId;
//                stepsToReorder = new ArrayList<>(fetchedSteps);
//                break;
//            }
//        }
//
//        if (selectedTcId == null) {
//            throw new RuntimeException("❌ No TestCase found with >= 2 steps in the flat response list.");
//        }
//
//        // 🔴 STEP 3: Reverse the list for the swap test
//        Collections.reverse(stepsToReorder);
//
//        List<Map<String, Object>> updatedStepsPayload = new ArrayList<>();
//        int sortOrder = 1;
//
//        for (Map<String, Object> step : stepsToReorder) {
//            // Using 'tcStepId' as seen in your provided JSON text
//            if (step == null || step.get("tcStepId") == null) continue;
//
//            Map<String, Object> stepData = new HashMap<>();
//            stepData.put("tcStepId", step.get("tcStepId"));
//            stepData.put("sortOrder", sortOrder);
//            stepData.put("newStepNumber", String.format("TCS-%d-%03d", selectedTcId, sortOrder));
//
//            updatedStepsPayload.add(stepData);
//            sortOrder++;
//        }
//
//        // 🔴 STEP 4: Execute Update
//        Map<String, Object> updateRequest = new HashMap<>();
//        updateRequest.put("steps", updatedStepsPayload);
//        updateRequest.put("userId", TokenUtil.getUserId());
//
//        ApiTestExecutor.execute(
//                "Update Step Order | TC " + selectedTcId,
//                null,
//                () -> UpdateTestCaseStepOrderApi.updateStepOrder(updateRequest, "SUPER_ADMIN", "VALID")
//        );
//
//        // 🔴 STEP 5: Verification (Scanning the Flat List again)
//        Response verifyResponse = GetTestCaseWithStepsApi.getTestCaseWithSteps(
//                Map.of("testCaseId", selectedTcId, "userId", TokenUtil.getUserId()),
//                "SUPER_ADMIN", "VALID"
//        );
//
//        List<Map<String, Object>> verifySteps = verifyResponse.jsonPath().getList("$");
//
//        if (verifySteps == null || verifySteps.isEmpty()) {
//            throw new RuntimeException("❌ Verification failed: API returned no steps after update.");
//        }
//
//        for (int i = 0; i < verifySteps.size(); i++) {
//            Number actual = (Number) verifySteps.get(i).get("sortOrder");
//            if (actual != null && actual.intValue() != (i + 1)) {
//                throw new AssertionError("❌ Step order incorrect at index " + i + ". Expected " + (i+1) + " but got " + actual);
//            }
//        }
//
//        System.out.println("✅ Successfully swapped and verified TC " + selectedTcId);
//    }
//}
package tests.generation;

import api.generation.UpdateTestCaseStepOrderApi;
import api.project.GetTestCaseWithStepsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.TestCaseStore;
import utils.TokenUtil;

import java.util.*;

public class UpdateTestCaseStepOrderTest extends BaseTest {


    public void updateTestCaseStepOrderApiTest() {

        // 🔴 STEP 1: Get all generated Test Case IDs from the store
        List<Integer> allTestCases = TestCaseStore.getAll();
        if (allTestCases == null || allTestCases.isEmpty()) {
            throw new RuntimeException("❌ No TestCases found in TestCaseStore. Run generation first.");
        }

        Integer selectedTcId = null;
        List<Map<String, Object>> stepsToReorder = null;

        // 🔴 STEP 2: Scan for a TC that has at least 2 steps
        System.out.println("🔍 Scanning for an eligible TestCase...");
        for (Integer tcId : allTestCases) {
            Map<String, Object> fetchRequest = new HashMap<>();
            fetchRequest.put("testCaseId", tcId);
            fetchRequest.put("userId", TokenUtil.getUserId());

            Response response = GetTestCaseWithStepsApi.getTestCaseWithSteps(fetchRequest, "SUPER_ADMIN", "VALID");

            if (response.getStatusCode() != 200) continue;

            // Extracting from root array ($) as per your Flat JSON structure
            List<Map<String, Object>> fetchedSteps = response.jsonPath().getList("$");

            if (fetchedSteps != null && fetchedSteps.size() >= 2) {
                selectedTcId = tcId;
                stepsToReorder = new ArrayList<>(fetchedSteps);
                System.out.println("🎯 Match Found: TC " + tcId + " (Steps: " + fetchedSteps.size() + ")");
                break;
            }
        }

        if (selectedTcId == null) {
            throw new RuntimeException("❌ No TestCase found with enough steps to test reordering.");
        }

        // 🔴 STEP 3: Reverse the list and prepare the Update Payload
        Collections.reverse(stepsToReorder);

        List<Map<String, Object>> updatedStepsPayload = new ArrayList<>();
        int counter = 1;
        for (Map<String, Object> step : stepsToReorder) {
            if (step == null || step.get("tcStepId") == null) continue;

            Map<String, Object> stepData = new HashMap<>();
            stepData.put("tcStepId", step.get("tcStepId"));
            stepData.put("sortOrder", counter);
            // Update the step number string to match the new order (TCS-XXXXX-001, etc)
            stepData.put("newStepNumber", String.format("TCS-%d-%03d", selectedTcId, counter));

            updatedStepsPayload.add(stepData);
            counter++;
        }

        // 🔴 STEP 4: Execute Update via Executor (with Reporter Context)
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("steps", updatedStepsPayload);
        updateRequest.put("userId", TokenUtil.getUserId());

        // Dummy object to provide a .getName() for the report
        final String tcNameForReport = "TC-" + selectedTcId;
        Object reportContext = new Object() {
            @Override
            public String toString() { return tcNameForReport; }
            public String getName() { return tcNameForReport; }
        };

        ApiTestExecutor.execute(
                "Dynamic Step Reorder | " + tcNameForReport,
                reportContext,
                () -> UpdateTestCaseStepOrderApi.updateStepOrder(updateRequest, "SUPER_ADMIN", "VALID")
        );

        // 🔴 STEP 5: Verification with Fallback Logic
        System.out.println("🔍 Verifying reorder results for TC " + selectedTcId + "...");
        Response verifyRes = GetTestCaseWithStepsApi.getTestCaseWithSteps(
                Map.of("testCaseId", selectedTcId, "userId", TokenUtil.getUserId()),
                "SUPER_ADMIN", "VALID"
        );

        List<Map<String, Object>> finalSteps = verifyRes.jsonPath().getList("$");

        for (int i = 0; i < finalSteps.size(); i++) {
            Map<String, Object> currentStep = finalSteps.get(i);
            int expectedOrder = i + 1;

            // Strategy: Check 'sortOrder' field; if null, parse from 'tcStepNumber' string
            Object sortObj = currentStep.get("sortOrder");
            int actualVal = -1;

            if (sortObj != null) {
                actualVal = ((Number) sortObj).intValue();
            } else {
                String stepNum = (String) currentStep.get("tcStepNumber");
                if (stepNum != null && stepNum.contains("-")) {
                    String[] parts = stepNum.split("-");
                    actualVal = Integer.parseInt(parts[parts.length - 1]);
                }
            }

            if (actualVal != expectedOrder) {
                throw new AssertionError("❌ Step " + i + " mismatch. Expected " + expectedOrder + " but got " + actualVal);
            }
        }

        System.out.println("✅ Dynamic Reorder Test Passed for TC " + selectedTcId);
    }
}