package tests.project;

import api.project.EditProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class EditProjectTest extends BaseTest {

    public void editProjectApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/project/editProject.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("EditProject.json missing or invalid");
        }

        String today =
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String nextMonth =
                LocalDate.now().plusMonths(1)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        System.out.println("DEBUG SelectedProjectId → " + ProjectStore.getSelectedProjectId());
        System.out.println("DEBUG ThreadProjectId → " + ProjectStore.getProjectId());

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            Map<String, Object> builtRequest =
                    buildEditProjectRequest(request, today, nextMonth);

            System.out.println("FINAL REQUEST → " + builtRequest);

            /* IMPORTANT: update report payload */
            tc.setRequest(builtRequest);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> EditProjectApi.editProject(
                            builtRequest,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }

    private Map<String, Object> buildEditProjectRequest(
            Map<String, Object> request,
            String startDate,
            String endDate) {

        Map<String, Object> map = new HashMap<>(request);

        /* -------- PROJECT ID -------- */

        if ("{{dynamic}}".equals(map.get("projectId"))) {

            Integer projectId = ProjectStore.getSelectedProjectId();

            if (projectId == null) {
                projectId = ProjectStore.getProjectId();
            }

            if (projectId == null) {
                throw new RuntimeException("Project ID missing. Ensure CreateProject step executed.");
            }

            map.put("projectId", projectId.toString());
        }

        /* -------- USER ID -------- */

        if ("{{dynamic}}".equals(map.get("userId"))) {

            String userId = ProjectStore.getUserId();

            if (userId == null) {

                Integer tokenUserId = TokenUtil.getUserId();

                if (tokenUserId == null) {
                    throw new RuntimeException("User ID missing");
                }

                userId = tokenUserId.toString();
                ProjectStore.setUserId(userId);
            }

            map.put("userId", userId);
        }

        /* -------- PROJECT NAME -------- */

        if ("{{dynamic}}".equals(map.get("projectName"))) {

            String projectName = ProjectStore.getSelectedProjectName();

            if (projectName == null) {
                projectName = "Automation_Project";
            }

            map.put("projectName", projectName);
        }

        /* -------- DATES -------- */

        if ("{{dynamic}}".equals(map.get("projectStartDate"))) {
            map.put("projectStartDate", startDate);
        }

        if ("{{dynamic}}".equals(map.get("projectEndDate"))) {
            map.put("projectEndDate", endDate);
        }

        /* -------- STORAGE -------- */

        if ("{{dynamic}}".equals(map.get("storageType"))) {

            String storageType;

            try {
                storageType = ProjectStore.getStorageType();
            } catch (Exception e) {
                storageType = "S3";
            }

            map.put("storageType", storageType);
        }

        /* -------- FRAMEWORK -------- */

        if ("{{dynamic}}".equals(map.get("webFramework"))) {

            String framework;

            try {
                framework = ProjectStore.getAutomationFramework();
            } catch (Exception e) {
                Object requestedFramework = map.get("webFramework");
                framework = requestedFramework != null ? requestedFramework.toString() : "C# + Playwright";
            }

            map.put("webFramework", framework);
        }

        /* -------- STATIC FIELDS REQUIRED BY API -------- */

        map.put("projectDescription", "automations");
        map.put("projectDomain", "Telecommunications");
        map.put("insightsBasedOnExistingAssets", 1);

        /* -------- CONNECTION DATA -------- */

        map.put("connectionId", ConnectionStore.getConnectionId());
        map.put("platform", ConnectionStore.getPlatform());

        System.out.println("Mapped Request → " + map);

        return map;
    }
}
