package tests.project;

import api.project.GetProjectUsersApi;
import api.project.GetProjectsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.ProjectUserStore;
import utils.TokenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetProjectUsersTest extends BaseTest {

    @DataProvider(name = "projectProvider")
    public Object[][] projectProvider() {

        // ✅ Ensure projects are loaded before using them
        if (ProjectStore.getAllProjectIds().isEmpty()) {

            Response response =
                    GetProjectsApi.getProjects(
                            Map.of("userId", TokenUtil.getUserId()),
                            "SUPER_ADMIN",
                            "VALID"
                    );

            List<Map<String, Object>> projects =
                    response.jsonPath().getList("$");

            ProjectStore.storeProjects(projects);
        }

        List<Integer> projectIds =
                new ArrayList<>(ProjectStore.getAllProjectIds());

        Object[][] data = new Object[projectIds.size()][1];

        for (int i = 0; i < projectIds.size(); i++) {
            data[i][0] = projectIds.get(i);
        }

        return data;
    }


    // ✅ TEST METHOD

    public void getUsersForProject(Integer projectId) {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getProjectUsers.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase baseTc =
                testData.getTestCases().get(0);

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(baseTc);

        tc.setTcId("TC_GET_PROJECT_USERS_" + projectId);
        tc.setName("Get users for project " + projectId);

        Map<String, Object> request =
                new HashMap<>((Map<String, Object>) tc.getRequest());

        request.put("projectId", projectId);
        request.put("userId", TokenUtil.getUserId());

        tc.setRequest(request);

        ApiTestExecutor.execute(
                testData.getScenario() + " | ProjectId=" + projectId,
                tc,
                () -> {

                    Response response =
                            GetProjectUsersApi.getProjectUsers(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    List<Map<String, Object>> users =
                            response.jsonPath().getList("users");

                    ProjectUserStore.storeUsers(projectId, users);

                    return response;
                }
        );
    }

    public void fetchProjectUsers() {

        TokenUtil.refreshToken();

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getProjectUsers.json",
                        ConnectionReport.class
                );

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            ConnectionReport.TestCase baseTc =
                    testData.getTestCases().get(0);

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(baseTc);

            Map<String, Object> request =
                    new HashMap<>((Map<String, Object>) tc.getRequest());

            request.put("projectId", projectId);
            request.put("userId", TokenUtil.getUserId());

            tc.setRequest(request);
            tc.setTcId("GET_USERS_" + projectId);
            tc.setName("Get Users for Project " + projectId);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetProjectUsersApi.getProjectUsers(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        List<Map<String, Object>> users =
                                response.jsonPath().getList("users");

                        ProjectUserStore.storeUsers(projectId, users);

                        return response;
                    }
            );
        }
    }
}
