package tests.roles;

import api.roles.DisableUsersByRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DisableUsersByRoleTest extends BaseTest {

    public void disableUsersByRole() {

        Report testData = JsonUtils.readJson(
                "testdata/rolesData/disableUsersByRole.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null || testData.getTestCases().isEmpty()) {
            throw new RuntimeException("❌ disableUsersByRole.json missing or malformed");
        }

        Integer roleId = RoleStore.getRoleId();

        if (roleId == null) {
            throw new RuntimeException("❌ RoleId not found in RoleStore");
        }

        System.out.println("🔎 RoleStore Data → " + roleId);

        // Get template test case
        Report.TestCase base = testData.getTestCases().get(0);
        Report.TestCase tc = new Report.TestCase(base);

        /* -------- BUILD REQUEST -------- */

        Map<String, Object> request = new HashMap<>();

        request.put("roleId", roleId);
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

        System.out.println("📦 DisableUsersByRole Payload → " + request);

        tc.setTcId("DISABLE_ROLE_" + roleId);
        tc.setName("Disable Users By Role | RoleId " + roleId);
        tc.setRequest(request);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response = DisableUsersByRoleApi.disableUsers(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    );

                    System.out.println("📡 Status → " + response.getStatusCode());
                    System.out.println("📡 Response → " + response.getBody().asString());

                    return response;
                }
        );
    }
}