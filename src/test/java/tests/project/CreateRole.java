package tests.project;

import api.roles.CreateRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class CreateRole extends BaseTest {

    public void createRoleTest() {

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/rolesData/createprojectrole.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new IllegalStateException("❌ Role test data is missing");
        }

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ Build request payload
                        Map<String, Object> req = new HashMap<>();

                        req.put("refProjectId", ProjectStore.getProjectId());
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));
                        req.put("roleName", TestDataGenerator.randomRoleName());
                        req.put("roleDescription", TestDataGenerator.randomDescription());

                        // ✅ Add payload to report
                        tc.setRequest(req);

                        // 🔥 CALL API
                        Response response = CreateRoleApi.createRole(
                                req,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ CreateRole API returned NULL response.");
                        }

                        System.out.println("📦 Create Role Payload → " + req);
                        System.out.println("📦 Create Role Response → " + response.asPrettyString());

                        // 🔥 EXTRACT roleId
                        Integer roleId = response.jsonPath().getInt("roleId");

                        if (roleId == null) {
                            throw new IllegalStateException(
                                    "❌ roleId not found in CreateRole response"
                            );
                        }

                        // ✅ Store roleId
                        RoleStore.setRoleId(roleId);

                        System.out.println("✅ Role ID Stored → " + roleId);

                        return response;
                    }
            );
        }
    }
}