package tests.project;

import api.testCase.InsertTestCaseApi;
import base.BaseTest;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTestCaseTest extends BaseTest {


    public void addTestCaseApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/testCase/addTestCase.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ addTestCase.json missing");
        }

        Integer projectId = ProjectStore.getSelectedProjectId();

        for (Report.TestCase tc : testData.getTestCases()) {

            // ✅ ALWAYS clone request
            Map<String, Object> request =
                    new HashMap<>((Map<String, Object>) tc.getRequest());

            // 🔁 userId
            request.put(
                    "userId",
                    TokenUtil.getUserId(tc.getRole())
            );

            // 🔁 TS ID (single source of truth)
            if ("DYNAMIC_TS".equals(request.get("refTSId"))) {

                List<Integer> tsIds = GeneratedTSStore.getAll();

                if (tsIds == null || tsIds.isEmpty()) {
                    throw new RuntimeException("❌ No TS available for Add Test Case");
                }

                request.put("refTSId", tsIds.get(0));
            }

            // 🔁 BR ID (project-aware & generation-aware)
            if ("DYNAMIC_BR".equals(request.get("refBrId"))) {

                Integer brId =
                        BusinessRequirementStore.getAnyBrId(projectId);

                if (brId == null) {
                    throw new RuntimeException("❌ No BR available for Add Test Case");
                }

                request.put("refBrId", brId);
            }

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        var response =
                                InsertTestCaseApi.insertTestCase(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        // ✅ STORE testCaseId (CRITICAL for Step 23)
                        if (response.getStatusCode() == 200) {

                            Integer testCaseId =
                                    response.jsonPath().getInt("testCaseId");

                            if (testCaseId != null) {
                                TestCaseStore.add(testCaseId);
                                System.out.println(
                                        "📦 Stored TestCase ID → " + testCaseId
                                );
                            }
                        }

                        return response;
                    }
            );
        }
    }
}
