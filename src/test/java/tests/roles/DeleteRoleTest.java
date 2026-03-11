package tests.roles;

import api.roles.DeleteRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DeleteRoleTest extends BaseTest {

    public void deleteRoles() {

        // Load JSON template
        Report testData = JsonUtils.readJson(
                "testdata/rolesData/deleteRole.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null || testData.getTestCases().isEmpty()) {
            throw new RuntimeException("❌ deleteRole.json is missing or invalid");
        }

        Report.TestCase baseTemplate = testData.getTestCases().get(0);

        RoleStore.getAll().forEach((projectId, roleIds) -> {

            if (roleIds == null) return;

            for (Integer roleId : roleIds) {

                // Copy template
                Report.TestCase tc = new Report.TestCase(baseTemplate);

                // Copy JSON request (contains action field)
                Map<String, Object> request =
                        new HashMap<>((Map<String, Object>) baseTemplate.getRequest());

                // Inject dynamic fields
                request.put("roleId", roleId);
                request.put("userId", TokenUtil.getUserId());

                // Attach request to test case (important for report)
                tc.setRequest(request);

                tc.setTcId("DEL_ROLE_" + roleId);
                tc.setName("Delete Role | RoleId: " + roleId);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> DeleteRoleApi.deleteRole(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        )
                );
            }
        });
    }
}