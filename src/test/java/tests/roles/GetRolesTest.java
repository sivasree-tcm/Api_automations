package tests.roles;

import api.roles.GetRolesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.RoleStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRolesTest extends BaseTest {

    public void getRolesForProject() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/rolesData/getRoles.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getRoles.json missing");
        }

        Integer projectId = ProjectStore.getProjectId();

        if (projectId == null) {
            throw new RuntimeException("❌ ProjectId not found in ProjectStore");
        }

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        Map<String, Object> request = new HashMap<>();

        request.put("refProjectId", projectId);
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

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

                    System.out.println("STATUS → " + response.getStatusCode());
                    System.out.println("BODY → " + response.asString());

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Get Roles API Failed → " + response.asString()
                        );
                    }

                    if (!response.getContentType().contains("application/json")) {
                        throw new RuntimeException(
                                "❌ API did not return JSON → " + response.asString()
                        );
                    }

                    List<Map<String, Object>> roles =
                            response.jsonPath().getList("");

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

                    Integer roleId =
                            (Integer) roles.get(0).get("roleId");

                    if (roleId != null) {

                        RoleStore.setRoleId(roleId);

                        System.out.println(
                                "📌 Stored Role ID → " + roleId
                        );
                    }

                    return response;
                }
        );
    }
}