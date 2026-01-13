package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.project.ProjectRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
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

        // 🔹 Override dynamic fields
        request.setUserId(String.valueOf(userId));
        request.setProjectCreatedBy(String.valueOf(userId));
        request.setProjectName("Automation_" + System.currentTimeMillis());

        // -------- CREATE PROJECT --------
        Response createResponse = ProjectApi.createProject(request);

        AllureUtil.attachText("Create Project URL", "/api/createProject");
        AllureUtil.attachJson(
                "Create Project Request",
                JsonUtils.toJson(request)
        );

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

        System.out.println("Verified project exists in My Projects list");
    }
}
