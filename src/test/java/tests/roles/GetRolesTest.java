package tests.roles;

import api.roles.GetRolesApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.RoleStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRolesTest extends BaseTest {

    public void getRolesForAllProjects() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/rolesData/getRoles.json",
                        ConnectionReport.class
                );

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            Map<String, Object> request = new HashMap<>();
            request.put("refProjectId", projectId);
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("GET_ROLES_" + projectId);
            tc.setName("Get Roles | Project " + projectId);
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetRolesApi.getRoles(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        // ✅ FIX 1: Correct way to read ROOT array
                        List<Map<String, Object>> roles =
                                response.jsonPath().getList("$");


                        // ✅ FIX 2: Null / empty protection
                        if (roles == null || roles.isEmpty()) {
                            System.out.println(
                                    "ℹ No roles found for project " + projectId
                            );
                            return response;
                        }

                        System.out.println(
                                "✅ Roles fetched for project " + projectId +
                                        " | Count = " + roles.size()
                        );

                        return response;
                    }
            );
        }
    }
}
