package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
import utils.TokenUtil;

import java.util.List;

public class GetMyProjectsTest extends BaseTest {

    @Test
    public void getAndVerifyMyProjectsTest() {

        // ✅ Dynamically fetched userId (from login)
        int userId = TokenUtil.getUserId();

        // -------- CALL GET MY PROJECTS API --------
        Response response = ProjectApi.getMyProjects();

        // 🔍 Debug
        System.out.println("Get My Projects Response:");
        response.prettyPrint();

        // ✅ Status validation
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected status code 200");

        // ✅ Allure evidence
        AllureUtil.attachText("Request URL", "/api/getMyProjects");
        AllureUtil.attachJson(
                "Request Body",
                "{ \"userId\": \"" + userId + "\" }"
        );
        AllureUtil.attachJson(
                "Get My Projects Response",
                response.asPrettyString()
        );

        // ✅ CORRECT JSON PATH (based on actual response)
        List<Integer> projectIds =
                response.jsonPath().getList("projects.projectId");

        Assert.assertNotNull(projectIds, "Project list should not be null");
        Assert.assertTrue(projectIds.size() > 0,
                "User should have at least one project");

        System.out.println("===== PROJECT IDs FOR USER " + userId + " =====");
        projectIds.forEach(id ->
                System.out.println("Project ID: " + id)
        );
    }
}
