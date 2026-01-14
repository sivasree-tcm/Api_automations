package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.project.ProjectRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExtentTestListener;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.List;

public class ProjectTest extends BaseTest {

    @Test
    public void createAndVerifyProjectTest() {

        ExtentTestListener.getTest()
                .info("Starting Create & Verify Project test");

        int userId = TokenUtil.getUserId();

        ProjectRequest request = JsonUtils.readJson(
                "testdata/project/createProject.json",
                ProjectRequest.class
        );

        request.setUserId(String.valueOf(userId));
        request.setProjectCreatedBy(String.valueOf(userId));
        request.setProjectName("Automation_" + System.currentTimeMillis());

        // 🔹 CREATE PROJECT
        Response createResponse = ProjectApi.createProject(request);

        Assert.assertEquals(createResponse.getStatusCode(), 200);

        int projectId = createResponse.jsonPath().getInt("project_id");
        Assert.assertTrue(projectId > 0, "Project ID should be generated");

        ExtentTestListener.getTest()
                .pass("Project created successfully with ID: " + projectId);

        // 🔹 VERIFY PROJECT IN MY PROJECTS
        Response listResponse = ProjectApi.getMyProjects();
        Assert.assertEquals(listResponse.getStatusCode(), 200);

        List<Integer> projectIds =
                listResponse.jsonPath().getList("projects.projectId");

        Assert.assertTrue(
                projectIds.contains(projectId),
                "Created project should appear in My Projects list"
        );

        ExtentTestListener.getTest()
                .pass("Verified project exists in My Projects list");
    }
}
