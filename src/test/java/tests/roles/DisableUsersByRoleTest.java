package tests.roles;

import api.roles.DisableUsersByRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class DisableUsersByRoleTest extends BaseTest {

    public void disableUsersByRole() {

        Report testData = JsonUtils.readJson(
                "testdata/rolesData/disableUsersByRole.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null || testData.getTestCases().isEmpty()) {
            throw new RuntimeException("❌ disableUsersByRole.json missing or malformed");
        }

        System.out.println("🔎 RoleStore Data → " + RoleStore.getAll());

        // Get the base template from JSON
        Report.TestCase base = testData.getTestCases().get(0);

        RoleStore.getAll().forEach((projectId, roleIds) -> {
            if (roleIds == null) return;

            for (Integer roleId : roleIds) {
                // 🛑 CRASH POINT: Ensure base.getRequest() is not null in ConnectionReport.java
                Report.TestCase tc = new Report.TestCase(base);

                /* -------- BUILD REQUEST -------- */
                Map<String, Object> request = new HashMap<>();
                request.put("roleId", roleId);
                // Dynamically get the userId for the admin performing the action
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
                            return response;
                        }
                );
            }
        });
    }
}