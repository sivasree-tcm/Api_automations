package tests.roles;

import api.roles.EditRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class EditRoleTest extends BaseTest {

    public void editRoles() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/rolesData/editRole.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ editRole.json missing");
        }

        System.out.println("🔎 RoleStore Data → " + RoleStore.getAll());

        RoleStore.getAll().forEach((projectId, roleIds) -> {

            for (Integer roleId : roleIds) {

                Report.TestCase base =
                        testData.getTestCases().get(0);

                Report.TestCase tc =
                        new Report.TestCase(base);

                /* ---------- BUILD REQUEST ---------- */

                Map<String, Object> request = new HashMap<>();

                request.put("roleId", roleId);
                request.put("roleName", RoleDataGenerator.generateRoleName());
                request.put("roleDescription", RoleDataGenerator.generateRoleDescription());
                request.put("userId", TokenUtil.getUserId(tc.getRole()));

                System.out.println("📦 EditRole Payload → " + request);

                tc.setTcId("EDIT_ROLE_" + roleId);
                tc.setName("Edit Role | RoleId " + roleId);
                tc.setRequest(request);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> {

                            Response response =
                                    EditRoleApi.editRole(
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
        });
    }
}