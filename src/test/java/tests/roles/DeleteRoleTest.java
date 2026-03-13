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

        Integer roleId = RoleStore.getRoleId();

        if (roleId == null) {
            throw new RuntimeException("❌ RoleId not found in RoleStore");
        }

        System.out.println("🔎 RoleStore Data → " + roleId);

        Report.TestCase baseTemplate = testData.getTestCases().get(0);

        // Copy template
        Report.TestCase tc = new Report.TestCase(baseTemplate);

        // Copy JSON request (contains static fields like action)
        Map<String, Object> request =
                (baseTemplate.getRequest() != null)
                        ? new HashMap<>((Map<String, Object>) baseTemplate.getRequest())
                        : new HashMap<>();

        // Inject dynamic fields
        request.put("roleId", roleId);
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

        // Attach request to test case
        tc.setRequest(request);

        tc.setTcId("DEL_ROLE_" + roleId);
        tc.setName("Delete Role | RoleId: " + roleId);

        System.out.println("📦 DeleteRole Payload → " + request);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response = DeleteRoleApi.deleteRole(
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