package tests.bdd;

import api.bdd.UpdateBddApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.TestCaseStore; // ✅ Import the store where ID was saved

import java.util.HashMap;
import java.util.Map;

public class UpdateBddTest extends BaseTest {

    public void updateBdd() {

        Integer userId = TokenUtil.getUserId();
        if (userId == null) {
            throw new RuntimeException("❌ UserId missing from TokenUtil.");
        }

        // ✅ Read JSON Test Data
        Report testData = JsonUtils.readJson(
                "testdata/bdd/updateBdd.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ updateBdd.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        // ✅ Clone request to modify it dynamically
                        Map<String, Object> request = (tc.getRequest() != null)
                                ? new HashMap<>((Map<String, Object>) tc.getRequest())
                                : new HashMap<>();

                        /* 🔗 DYNAMIC LINKING: Fetch stored TestCaseId */
                        if ("DYNAMIC_TC".equals(request.get("testCaseId"))) {

                            // Get the ID that was stored in Step 22 (InsertTestCaseTest)
                            Integer testCaseId = TestCaseStore.getAnyTestCaseId();

                            if (testCaseId == null) {
                                throw new RuntimeException("❌ No TestCase ID found in Store! Ensure InsertTestCaseTest ran first.");
                            }

                            request.put("testCaseId", testCaseId);
                            System.out.println("🔗 Injected Dynamic TestCase ID → " + testCaseId);
                        }

                        /* ✅ Inject dynamic userId */
                        request.put("userId", userId.toString());

                        // Update TC object with the new dynamic request for the report
                        tc.setRequest(request);

                        System.out.println("📦 Update BDD Payload → " + request);

                        Response response = UpdateBddApi.updateBdd(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        return response;
                    }
            );
        }
    }
}