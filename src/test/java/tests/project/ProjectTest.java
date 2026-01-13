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

        int userId = TokenUtil.getUserId();

        // 🔹 Load JSON → POJO
        ProjectRequest request =
                JsonUtils.readJson(
                        "testdata/project/createProject.json",
                        ProjectRequest.class
                );

        // 🔹 Dynamic overrides
        request.setUserId(String.valueOf(userId));
        request.setProjectCreatedBy(String.valueOf(userId));
        request.setProjectName("Automation_" + System.currentTimeMillis());

        // ================= CREATE PROJECT =================
        Response createResponse = ProjectApi.createProject(request);

        // 🔹 Extent logging
        ExtentTestListener.getTest().info("HTTP Method: POST");
        ExtentTestListener.getTest().info("Endpoint: /api/createProject");

        ExtentTestListener.getTest().info(
                "<b>Request Payload:</b><pre>" +
                        JsonUtils.toJson(request) + "</pre>"
        );

        ExtentTestListener.getTest().info(
                "<b>Expected Status Code:</b> 200"
        );

        ExtentTestListener.getTest().info(
                "<b>Actual Response:</b><pre>" +
                        createResponse.asPrettyString() + "</pre>"
        );

        // 🔹 Assertions
        Assert.assertEquals(createResponse.getStatusCode(), 200);

        int projectId = createResponse.jsonPath().getInt("project_id");
        Assert.assertTrue(projectId > 0);

        ExtentTestListener.getTest()
                .pass("Project created successfully with ID: " + projectId);

        // ================= VERIFY PROJECT =================
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
