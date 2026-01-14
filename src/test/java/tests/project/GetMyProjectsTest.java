package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExtentTestListener;
import utils.TokenUtil;

import java.util.List;

public class GetMyProjectsTest extends BaseTest {

    @Test
    public void getAndVerifyMyProjectsTest() {

        ExtentTestListener.getTest()
                .info("Starting Get My Projects test");

        int userId = TokenUtil.getUserId();

        Response response = ProjectApi.getMyProjects();

        Assert.assertEquals(response.getStatusCode(), 200);

        List<Integer> projectIds =
                response.jsonPath().getList("projects.projectId");

        Assert.assertNotNull(projectIds,
                "Project list should not be null");

        Assert.assertTrue(
                projectIds.size() > 0,
                "User should have at least one project"
        );

        ExtentTestListener.getTest()
                .pass("User " + userId +
                        " has " + projectIds.size() + " projects");
    }
}
