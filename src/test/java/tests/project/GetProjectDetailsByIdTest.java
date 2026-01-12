package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
import utils.TokenUtil;

public class GetProjectDetailsByIdTest extends BaseTest {

    @Test
    public void getProjectDetailsByProjectIdTest() {

        int projectId = 128; // existing projectId

        // -------- CALL API --------
        Response response = ProjectApi.getProjectDetailsByProjectId(projectId);

        // 🔍 Debug
        System.out.println("Get Project Details Response:");
        response.prettyPrint();

        // ✅ Status validation
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected status code 200");

        // ✅ Allure evidence
        AllureUtil.attachText(
                "Request URL",
                "/api/getProjectDetailsByProjectId"
        );
        AllureUtil.attachJson(
                "Request Body",
                "{ \"userId\": \"" + TokenUtil.getUserId() +
                        "\", \"projectId\": \"" + projectId + "\" }"
        );
        AllureUtil.attachJson(
                "Get Project Details Response",
                response.asPrettyString()
        );

        // ✅ CORRECT ASSERTIONS (FIXED JSON PATH)
        Integer returnedProjectId =
                response.jsonPath().getInt("results[0].projectId");

        Assert.assertNotNull(returnedProjectId,
                "Returned projectId should not be null");

        Assert.assertEquals(
                returnedProjectId.intValue(),
                projectId,
                "Returned projectId should match request"
        );

        String projectName =
                response.jsonPath().getString("results[0].projectName");

        Assert.assertNotNull(projectName,
                "Project name should not be null");

        Integer returnedUserId =
                response.jsonPath().getInt("results[0].userId");

        Assert.assertEquals(
                returnedUserId.intValue(),
                TokenUtil.getUserId(),
                "userId should match logged-in user"
        );

        System.out.println(
                "Verified project details for projectId: " + projectId
        );
    }
}
