package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import models.project.ProjectRequest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.*;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.*;

public class createprojectflow extends BaseTest {

    @Test
    public void projectApiTest() {
        ProjectTestData testData = JsonUtils.readJson("testdata/project/Project.json", ProjectTestData.class);
        execute(testData, testData.getTestCases());
    }

    private void execute(ProjectTestData testData, List<ProjectTestData.TestCase> cases) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dynamicProjectName = "Automation_" + timestamp;

        for (ProjectTestData.TestCase tc : cases) {
            ProjectRequest request = tc.getRequest();

            if (request != null) {
                int userId = TokenUtil.getUserId(tc.getRole());
                request.setUserId(String.valueOf(userId));
                request.setProjectCreatedBy(String.valueOf(userId));

                if (request.getProjectName() != null && request.getProjectName().contains("{{projectName}}")) {
                    request.setProjectName(dynamicProjectName);
                }
                if (request.getConnectionId() != null && request.getConnectionId().contains("{{connectionId}}")) {
                    request.setConnectionId(String.valueOf(ConnectionStore.getConnectionId()));
                }
                if (request.getPlatform() != null && request.getPlatform().contains("{{platform}}")) {
                    request.setPlatform(ConnectionStore.getPlatform());
                }

                request.setProjectStartDate(LocalDate.now().toString());
                request.setProjectEndDate(LocalDate.now().plusDays(30).toString());
            }

            ApiTestExecutor.execute(testData.getScenario(), tc, () -> {
                Response res = ProjectApi.createProject(tc.getRequest(), tc.getRole());

                if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
                    Object idObj = res.jsonPath().get("project_id");
                    if (idObj != null) {
                        int projectId = Integer.parseInt(idObj.toString());
                        String projectName = tc.getRequest().getProjectName();

                        // ✅ FIX: Manually register the project in the Map so getProjectName() works later
                        Map<String, Object> projectData = new HashMap<>();
                        projectData.put("projectId", projectId);
                        projectData.put("projectName", projectName);
                        ProjectStore.storeProjects(Collections.singletonList(projectData));

                        // ✅ Set as active selection
                        ProjectStore.setSelectedProject(projectId);
                        ProjectStore.setProjectId(projectId);
                        ProjectFileLogger.logSelectedProject();

                        System.out.println("✅ Project Created & Registered → ID: " + projectId + ", Name: " + projectName);
                    }
                } else {
                    System.err.println("❌ Failed: " + res.getStatusCode() + " | " + res.asString());
                }
                return res;
            });
        }
    }
}