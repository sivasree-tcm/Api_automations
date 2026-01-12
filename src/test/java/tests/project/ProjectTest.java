package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.project.ProjectRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;

public class ProjectTest extends BaseTest {

    @Test
    public void createProjectTest() {

        ProjectRequest request = new ProjectRequest();
        request.setUserId("33");
        request.setProjectName("Automation_" + System.currentTimeMillis());
        request.setProjectDescription("Automation project");
        request.setProjectSummary(null);
        request.setProjectStartDate("2026-01-08");
        request.setProjectEndDate("2026-01-14");
        request.setProjectCreatedBy("33");
        request.setWebFramework("Playwright_Java");
        request.setMobileFrameworks("Appium_Java");
        request.setAutonomous(0);
        request.setProjectDomain("Agriculture");
        request.setTestType(null);
        request.setInsightsBasedOnExistingAssets("no");
        request.setRefOrgId("1");
        request.setStorageType("S3");
        request.setConnections("TickingMinds");
        request.setDevopsProjectName("f3c9e398-fa65-4695-bc19-b5172acd23a6");
        request.setTeams("Insurance Team");

        Response response = ProjectApi.createProject(request);

        // ✅ Attach evidence to Allure
        AllureUtil.attachText("Request URL", "/api/createProject");
        AllureUtil.attachJson("Create Project Request", request.toString());
        AllureUtil.attachJson("Create Project Response", response.asPrettyString());

        // ✅ Assertions
        Assert.assertEquals(response.getStatusCode(), 200);

        Integer projectId = response.jsonPath().getInt("project_id");
        Assert.assertTrue(projectId > 0, "Project ID should be greater than 0");

        System.out.println("Project created successfully with ID: " + projectId);
    }
}
