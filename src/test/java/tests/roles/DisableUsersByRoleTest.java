package tests.roles;

import api.roles.DisableUsersByRoleApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class DisableUsersByRoleTest extends BaseTest {

    public void disableUsersByRole() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/rolesData/disableUsersByRole.json",
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
                request.put("userId", TokenUtil.getUserId());

                tc.setTcId("DISABLE_ROLE_" + roleId);
                tc.setName("Disable Users By Role | RoleId " + roleId);
                tc.setRequest(request);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> {

                            Response response =
                                    DisableUsersByRoleApi.disableUsers(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            System.out.println(
                                    "🚫 Users disabled for role → " + roleId
                            );

                            return response;
                        }
                );
            }
        });
    }
}
