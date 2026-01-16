//package tests.project;
//
//import api.project.ProjectApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import utils.ExtentTestListener;
//import utils.TokenUtil;
//
//public class GetProjectDetailsByIdTest extends BaseTest {
//
//    @Test
//    public void getProjectDetailsByProjectIdTest() {
//
//        ExtentTestListener.getTest()
//                .info("Starting Get Project Details by ID test");
//
//        int projectId = 128; // existing projectId
//
//        Response response =
//                ProjectApi.getProjectDetailsByProjectId(projectId);
//
//        Assert.assertEquals(response.getStatusCode(), 200);
//
//        Integer returnedProjectId =
//                response.jsonPath().getInt("results[0].projectId");
//
//        Assert.assertNotNull(returnedProjectId);
//        Assert.assertEquals(
//                returnedProjectId.intValue(),
//                projectId,
//                "Project ID should match"
//        );
//
//        String projectName =
//                response.jsonPath().getString("results[0].projectName");
//
//        Assert.assertNotNull(projectName,
//                "Project name should not be null");
//
//        Integer returnedUserId =
//                response.jsonPath().getInt("results[0].userId");
//
//        Assert.assertEquals(
//                returnedUserId.intValue(),
//                TokenUtil.getUserId(),
//                "User ID should match logged-in user"
//        );
//
//        ExtentTestListener.getTest()
//                .pass("Verified project details for projectId: " + projectId);
//    }
//}
