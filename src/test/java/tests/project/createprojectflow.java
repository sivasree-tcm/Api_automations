package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.*;

public class createprojectflow extends BaseTest {

    public void projectApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/project/Project.json",
                Report.class
        );

        execute(testData, testData.getTestCases());
    }

    private void execute(Report testData, List<Report.TestCase> cases) {

        String timestamp = String.valueOf(System.currentTimeMillis());
        String dynamicProjectName = "Automation_" + timestamp;

        for (Report.TestCase tc : cases) {

            Map<String, Object> request =
                    new HashMap<>((Map<String, Object>) tc.getRequest());

            if (request != null) {

                int userId = TokenUtil.getUserId(tc.getRole());

                request.put("userId", String.valueOf(userId));
                request.put("projectCreatedBy", String.valueOf(userId));

                if (request.get("projectName") != null &&
                        request.get("projectName").toString().contains("{{projectName}}")) {

                    request.put("projectName", dynamicProjectName);
                }

                if (request.get("connectionId") != null &&
                        request.get("connectionId").toString().contains("{{connectionId}}")) {

                    request.put("connectionId",
                            String.valueOf(ConnectionStore.getConnectionId()));
                }

                if (request.get("platform") != null &&
                        request.get("platform").toString().contains("{{platform}}")) {

                    request.put("platform",
                            ConnectionStore.getPlatform());
                }

                request.put("projectStartDate",
                        LocalDate.now().toString());

                request.put("projectEndDate",
                        LocalDate.now().plusDays(30).toString());
            }

            // ✅ store modified payload back to report
            tc.setRequest(request);

            ApiTestExecutor.execute(testData.getScenario(), tc, () -> {

                Response res =
                        ProjectApi.createProject(request, tc.getRole());

                if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {

                    Object idObj = res.jsonPath().get("project_id");

                    if (idObj != null) {

                        int projectId = Integer.parseInt(idObj.toString());
                        String projectName =
                                request.get("projectName").toString();

                        Map<String, Object> projectData =
                                new HashMap<>();

                        projectData.put("projectId", projectId);
                        projectData.put("projectName", projectName);

                        ProjectStore.storeProjects(
                                Collections.singletonList(projectData)
                        );

                        ProjectStore.setSelectedProject(projectId);
                        ProjectStore.setProjectId(projectId);

                        ProjectFileLogger.logSelectedProject();

                        System.out.println(
                                "✅ Project Created & Registered → ID: "
                                        + projectId + ", Name: " + projectName
                        );
                    }
                }
                else {

                    System.err.println(
                            "❌ Failed: " + res.getStatusCode()
                                    + " | " + res.asString()
                    );
                }

                return res;
            });
        }
    }
}