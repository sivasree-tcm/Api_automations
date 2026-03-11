package tests.roles;

import api.roles.CreateRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class CreateRoleTest extends BaseTest {


    public void createRoleTest() {

        Report testData = JsonUtils.readJson(
                "testdata/rolesData/createRole.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new IllegalStateException("❌ Role test data is missing");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ Build request fresh
                        Map<String, Object> req = new HashMap<>();

                        req.put("refProjectId", ProjectStore.getProjectId());
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));
                        req.put("roleName", TestDataGenerator.randomRoleName());
                        req.put("roleDescription",
                                TestDataGenerator.randomDescription());

                        // 🔥 CALL API
                        Response response = CreateRoleApi.createRole(
                                req,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        // 🔥 EXTRACT & STORE roleId
                        Integer roleId = response.jsonPath().getInt("roleId");

                        if (roleId == null) {
                            throw new IllegalStateException(
                                    "❌ roleId not found in CreateRole response"
                            );
                        }

                        RoleStore.setRoleId(roleId);

                        return response;
                    }
            );
        }
    }
}