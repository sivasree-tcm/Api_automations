package tests.generation;

import api.testScenario.UpdateTestScenarioApi;
import base.BaseTest;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;
import java.util.HashMap;
import java.util.Map;

public class UpdateTestScenarioTest extends BaseTest {

    public void updateTestScenarioApiTest() {
        Report testData = JsonUtils.readJson(
                "testdata/testScenario/updateTestScenario.json",
                Report.class
        );

        for (Report.TestCase tc : testData.getTestCases()) {
            Map<String, Object> request = new HashMap<>();

            // ✅ Dynamic: Grabbing the LATEST scenario ID
            Integer tsId = TestScenarioStore.getLastTsId();
            Integer projectId = ProjectStore.getProjectId();

            if (tsId == null) {
                throw new RuntimeException("❌ Failure: No Test Scenario found to update.");
            }

            request.put("testScenarioId", tsId);
            request.put("projectId", projectId);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));
            request.put("tsDescription", "Updated Scenario via Automation | ID: " + tsId);
            request.put("tsPriority", "high");
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

            System.out.println("✅ Successfully sent update for Last TS ID: " + tsId);
        }
    }
}