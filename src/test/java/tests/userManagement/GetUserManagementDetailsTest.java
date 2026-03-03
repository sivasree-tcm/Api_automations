package tests.userManagement;

import api.userManagement.GetUserManagementDetailsApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUserManagementDetailsTest extends BaseTest {

    public void getUserManagementDetails() {

        /* ===============================
           1️⃣ Resolve Dynamic Values
           =============================== */

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        if (projectId == null) {
            throw new RuntimeException("❌ ProjectId missing in ProjectStore.");
        }

        if (userId == null) {
            throw new RuntimeException("❌ UserId missing.");
        }

        /* ===============================
           2️⃣ Load JSON Metadata
           =============================== */

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/UserManagement/getUserManagementDetails.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ JSON missing or invalid.");
        }

        /* ===============================
           3️⃣ Execute API
           =============================== */

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> request = new HashMap<>();
                        request.put("projectId", projectId);
                        request.put("userId", userId);

                        tc.setRequest(request);

                        System.out.println("📦 Get User Management Payload → " + request);

                        Response response =
                                GetUserManagementDetailsApi.getUserManagementDetails(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ API failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        List<Map<String, Object>> results =
                                response.jsonPath().getList("$");

                        if (results == null || results.isEmpty()) {
                            throw new RuntimeException(
                                    "❌ No user management data returned."
                            );
                        }

                        // Basic Validation
                        Map<String, Object> firstUser = results.get(0);

                        if (firstUser.get("userId") == null) {
                            throw new RuntimeException("❌ userId missing in response.");
                        }

                        if (firstUser.get("roleName") == null) {
                            throw new RuntimeException("❌ roleName missing in response.");
                        }

                        System.out.println("✅ User Management Data Retrieved → "
                                + firstUser.get("userEmailId"));

                        return response;
                    }
            );
        }
    }
}