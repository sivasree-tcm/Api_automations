package tests.roles;

import api.roles.DeleteRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DeleteRoleTest extends BaseTest {

    public void deleteRoles() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/rolesData/deleteRole.json",
                        ConnectionReport.class
                );

        RoleStore.getAll().forEach((projectId, roleIds) -> {

            for (Integer roleId : roleIds) {

                ConnectionReport.TestCase tc =
                        new ConnectionReport.TestCase(
                                testData.getTestCases().get(0)
                        );

                Map<String, Object> request = new HashMap<>();
                request.put("roleId", roleId);
                request.put("userId", TokenUtil.getUserId());
                request.put("action", "delete");

                tc.setTcId("DEL_ROLE_" + roleId);
                tc.setName("Delete Role | RoleId " + roleId);
                tc.setRequest(request);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> {

                            Response response =
                                    DeleteRoleApi.deleteRole(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            System.out.println(
                                    "🗑 Role deleted → roleId=" + roleId
                            );

                            return response;
                        }
                );
            }
        });
    }
}
