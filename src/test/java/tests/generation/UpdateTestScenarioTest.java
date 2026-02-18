//package tests.generation;
//
//import api.generation.UpdateTestScenarioApi;
//import base.BaseTest;
//import org.testng.annotations.Test;
//import tests.connection.ConnectionReport;
//import tests.user.ApiTestExecutor;
//import utils.*;
//
//import java.util.Map;
//
//public class UpdateTestScenarioTest extends BaseTest {
//
//    @Test
//    public void updateTestScenarioApiTest() {
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/generation/updateTestScenario.json",
//                        ConnectionReport.class
//                );
//
//        for (ConnectionReport.TestCase tc : testData.getTestCases()) {
//
//            Map<String, Object> request =
//                    (Map<String, Object>) tc.getRequest();
//
//            // 🔁 Dynamic userId
//            request.put(
//                    "userId",
//                    TokenUtil.getUserId(tc.getRole())
//            );
//
//            // 🔁 Dynamic projectId
//            if ("DYNAMIC_PROJECT".equals(request.get("projectId"))) {
//                request.put(
//                        "projectId",
//                        ProjectStore.getProjectId()
//                );
//            }
//
//            // 🔁 Dynamic testScenarioId (optional – if stored)
//            if ("DYNAMIC_TS".equals(request.get("testScenarioId"))) {
//                Integer tsId = TestScenarioStore.getAnyTsId();
//                if (tsId == null) {
//                    throw new RuntimeException("❌ No TestScenarioId available");
//                }
//                request.put("testScenarioId", tsId);
//            }
//
//            ApiTestExecutor.execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> UpdateTestScenarioApi.updateTestScenario(
//                            request,
//                            tc.getRole(),
//                            tc.getAuthType()
//                    )
//            );
//        }
//    }
//}
package tests.generation;

import api.generation.UpdateTestScenarioApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class UpdateTestScenarioTest extends BaseTest {

    @Test
    public void updateTestScenarioApiTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/generation/updateTestScenario.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ updateTestScenario test data missing");
        }

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            // 🔴 HARDCODED VALUES (AS REQUESTED)
            request.put("testScenarioId", 26479);
            request.put(
                    "tsDescription",
                    "Verify that the system allows tracking of project progress by enabling updates to project milestones, deliverables, and status indicators for each grantee."
            );
            request.put("tsPriority", "high");
            request.put("userId", 33);
            request.put("projectId", 1636);
            request.put("tcGenerate", 0);

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> UpdateTestScenarioApi.updateTestScenario(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
