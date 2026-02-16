package tests.project;

import api.project.GetProjectDetailsApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectFileLogger;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class GetProjectDetailsTest extends BaseTest {

    public void fetchProjectDetails(Integer expectedProjectId) {

        if (!ProjectStore.containsProject(expectedProjectId)) {
            System.out.println("❌ Project not found: " + expectedProjectId);
            return;
        }
        // ✅ SET selected project here
        ProjectStore.setSelectedProject(expectedProjectId);

        // ✅ LOG selected project (CRITICAL FIX)
        ProjectFileLogger.logSelectedProject();


        System.out.println("✅ Selected Project ID set to: " + expectedProjectId);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getProjectDetails.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        Map<String, Object> request = new HashMap<>();
        request.put("userId", TokenUtil.getUserId());
        request.put("projectId", expectedProjectId);
        request.put("isSuperAdmin", true);

        tc.setRequest(request);
        tc.setTcId("GET_PROJECT_DETAILS_" + expectedProjectId);
        tc.setName("Get Project Details - " + expectedProjectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> GetProjectDetailsApi.getProjectDetails(
                        request,
                        tc.getRole(),
                        tc.getAuthType()
                )
        );

    }
}
