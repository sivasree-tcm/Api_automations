package tests.roles;

import api.roles.EditRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
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

        RoleStore.getAll().forEach((projectId, roleIds) -> {

            for (Integer roleId : roleIds) {

                Report.TestCase tc =
                        new Report.TestCase(
                                testData.getTestCases().get(0)
                        );

                Map<String, Object> request = new HashMap<>();
                request.put("roleId", roleId);
                request.put("roleName", RoleDataGenerator.generateRoleName());
                request.put("roleDescription", RoleDataGenerator.generateRoleDescription());
                request.put("userId", TokenUtil.getUserId());


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

                            System.out.println(
                                    "✏️ Role updated → roleId=" + roleId
                            );

                            return response;
                        }
                );
            }
        });
    }
}
