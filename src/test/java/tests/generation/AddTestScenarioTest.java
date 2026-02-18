//package tests.generation;
//
//import api.generation.AddTestScenarioApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.annotations.Test;
//import tests.connection.ConnectionReport;
//import tests.user.ApiTestExecutor;
//import utils.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AddTestScenarioTest extends BaseTest {
//
//    @Test
//    public void addTestScenarioApiTest() {
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/generation/addTestScenario.json",
//                        ConnectionReport.class
//                );
//
//        if (testData == null || testData.getTestCases() == null) {
//            throw new RuntimeException("❌ addTestScenario test data missing");
//        }
//
//        Integer projectId = ProjectStore.getSelectedProjectId();
//
//        // ✅ Get any BR for the project
//        List<Integer> brIds =
//                BusinessRequirementStore.getBrIds(projectId);
//
//        if (brIds == null || brIds.isEmpty()) {
//            throw new RuntimeException(
//                    "❌ No BRs available for project " + projectId
//            );
//        }
//
//        Integer brId = brIds.get(0); // pick one BR
//
//        for (ConnectionReport.TestCase tc : testData.getTestCases()) {
//
//            Map<String, Object> request = new HashMap<>();
//
//            request.put(
//                    "tsDescription",
//                    "Verify that the system allows tracking of project progress by enabling updates to project milestones, deliverables, and status indicators."
//            );
//            request.put("refBrId", brId);
//            request.put("refProjectId", projectId);
//            request.put("tsPriority", "medium");
//            request.put("tsCreationMethod", "manual");
//            request.put("userId", TokenUtil.getUserId(tc.getRole()));
//
//            tc.setRequest(request);
//
//            ApiTestExecutor.execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> {
//
//                        Response response =
//                                AddTestScenarioApi.addTestScenario(
//                                        request,
//                                        tc.getRole(),
//                                        tc.getAuthType()
//                                );
//
//                        // ✅ STORE TS ID FOR NEXT STEPS
//                        if (response.getStatusCode() == 200) {
//
//                            Integer tsId =
//                                    response.jsonPath().getInt("testScenarioId");
//
//                            if (tsId != null) {
//                                TestScenarioStore.addTs(brId, tsId);
//                                System.out.println(
//                                        "✅ Test Scenario created → TS ID: " + tsId
//                                );
//                            }
//                        }
//
//                        return response;
//                    }
//            );
//        }
//    }
//}
package tests.generation;

import api.generation.AddTestScenarioApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class AddTestScenarioTest extends BaseTest {


    public void addTestScenarioApiTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/generation/addTestScenario.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ addTestScenario test data missing");
        }

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // 🔴 HARDCODED FOR TESTING
            request.put(
                    "tsDescription",
                    "Verify that the system allows tracking of project progress by enabling updates to project milestones, deliverables, and status indicators for each grantee"
            );
            request.put("refBrId", 6425);
            request.put("refProjectId", 1636);
            request.put("tsPriority", "medium");
            request.put("tsCreationMethod", "manual");
            request.put("userId", 28);

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> AddTestScenarioApi.addTestScenario(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
