package tests.roles;

import api.roles.CreateRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class CreateRoleTest extends BaseTest {

    public void createRolesForAllProjects() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/rolesData/createRole.json",
                        Report.class
                );

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            Report.TestCase tc =
                    new Report.TestCase(
                            testData.getTestCases().get(0)
                    );

            String roleName = RoleDataGenerator.generateRoleName();
            String roleDesc = RoleDataGenerator.generateRoleDescription();

            Map<String, Object> request = new HashMap<>();
            request.put("refProjectId", projectId);
            request.put("roleName", roleName);
            request.put("roleDescription", roleDesc);
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("CREATE_ROLE_" + projectId);
            tc.setName("Create Role for Project " + projectId);
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                CreateRoleApi.createRole(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        Integer roleId =
                                response.jsonPath().getInt("roleId");

                        // ✅ Store role for later use
                        RoleStore.store(projectId, roleId);

                        System.out.println(
                                "✅ Role created → project=" + projectId +
                                        ", roleId=" + roleId
                        );

                        return response;
                    }
            );
        }
    }
}
