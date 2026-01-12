package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.project.ProjectRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
import utils.TokenUtil;

import java.util.List;

public class ProjectTest extends BaseTest {

    @Test
    public void createAndVerifyProjectTest() {

        int userId = TokenUtil.getUserId();

        // -------- CREATE PROJECT --------
        ProjectRequest request = new ProjectRequest();
        request.setUserId(String.valueOf(userId));
        request.setProjectCreatedBy(String.valueOf(userId));
        request.setProjectName("Automation_" + System.currentTimeMillis());
        request.setProjectDescription("Automation project");
        request.setProjectSummary(null);
        request.setProjectStartDate("2026-01-08");
        request.setProjectEndDate("2026-01-14");
        request.setWebFramework("Playwright_Java");
        request.setMobileFrameworks("Appium_Java");
        request.setAutonomous(0);
        request.setProjectDomain("Agriculture");
        request.setTestType(null);
        request.setInsightsBasedOnExistingAssets("no");
        request.setRefOrgId("1");
        request.setStorageType("S3");
        request.setConnections("TickingMinds");
        request.setDevopsProjectName("0a4a3da6-6779-4f7c-8d76-4d7e88811ae4");
        request.setTeams("Insurance Team");

        Response createResponse = ProjectApi.createProject(request);

        AllureUtil.attachText("Create Project URL", "/api/createProject");
        AllureUtil.attachJson("Create Project Request", request.toString());
        AllureUtil.attachJson("Create Project Response", createResponse.asPrettyString());

        Assert.assertEquals(createResponse.getStatusCode(), 200);

        int projectId = createResponse.jsonPath().getInt("project_id");
        Assert.assertTrue(projectId > 0);

        System.out.println("Project created successfully with ID: " + projectId);

        // -------- VERIFY VIA GET MY PROJECTS --------
        Response listResponse = ProjectApi.getMyProjects();

        AllureUtil.attachText("Get My Projects URL", "/api/getMyProjects");
        AllureUtil.attachJson("Get My Projects Response", listResponse.asPrettyString());

        Assert.assertEquals(listResponse.getStatusCode(), 200);

        List<Integer> projectIds =
                listResponse.jsonPath().getList("projects.projectId");

        Assert.assertTrue(
                projectIds.contains(projectId),
                "Created project should appear in My Projects list"
        );

        System.out.println("Verified project exists in project list");
    }
}
