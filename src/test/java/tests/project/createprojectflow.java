package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import models.project.ProjectRequest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.ConnectionStore;
import utils.JsonUtils;
import utils.TokenUtil;
import utils.ProjectStore;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;

public class createprojectflow extends BaseTest {

    @Test
    public void projectApiTest() {
        ProjectTestData testData = JsonUtils.readJson(
                "testdata/project/Project.json",
                ProjectTestData.class
        );
        execute(testData, testData.getTestCases());
    }

    private void execute(ProjectTestData testData, List<ProjectTestData.TestCase> cases) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String duplicateProjectName = "DuplicateTest_" + timestamp;

        for (ProjectTestData.TestCase tc : cases) {
            ProjectRequest request = tc.getRequest();

            if (request != null) {
                // 1. Dynamic User/Creator Injection
                int userId = TokenUtil.getUserId(tc.getRole());
                request.setUserId(String.valueOf(userId));
                request.setProjectCreatedBy(String.valueOf(userId));

                // 2. Dynamic Project Name
                if (request.getProjectName() != null && request.getProjectName().contains("{{projectName}}")) {
                    request.setProjectName(duplicateProjectName);
                }

                // 3. Dynamic Connection & Platform Injection
                if (request.getConnectionId() != null && request.getConnectionId().contains("{{connectionId}}")) {
                    request.setConnectionId(String.valueOf(ConnectionStore.getConnectionId()));
                }
                if (request.getPlatform() != null && request.getPlatform().contains("{{platform}}")) {
                    request.setPlatform(ConnectionStore.getPlatform());
                }

                // 4. Dynamic Date Generation
                request.setProjectStartDate(LocalDate.now().toString());
                request.setProjectEndDate(LocalDate.now().plusDays(30).toString());
            }

            // ✅ Execution with Correct ID Extraction
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        Response res = ProjectApi.createProject(tc.getRequest(), tc.getRole());

                        if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
                            try {
                                // ✅ FIX: Extraction matches "project_id" in response
                                Object idObj = res.jsonPath().get("project_id");
                                if (idObj != null) {
                                    int id = Integer.parseInt(idObj.toString());
                                    ProjectStore.setProjectId(id);
                                    System.out.println("SUCCESS: Saved Project ID " + id);
                                }
                            } catch (Exception e) {
                                System.err.println("WARN: Parse failed for project_id. Full body: " + res.asString());
                            }
                        } else {
                            System.err.println("API ERROR: " + res.getStatusCode() + " - " + res.asString());
                        }
                        return res;
                    }
            );
        }
    }
}