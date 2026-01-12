//package tests.project;
//
//import api.project.ProjectApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import utils.AllureUtil;
//import utils.TokenUtil;
//
//import java.util.List;
//
//public class GetProjectsTest extends BaseTest {
//
//    @Test
//    public void getProjectsTest() {
//
//        // ✅ Dynamic userId from login
//        int userId = TokenUtil.getUserId();
//
//        // -------- CALL API --------
//        Response response = ProjectApi.getProjects();
//
//        // 🔍 Debug
//        System.out.println("Get Projects Response:");
//        response.prettyPrint();
//
//        // ✅ Status validation
//        Assert.assertEquals(
//                response.getStatusCode(),
//                200,
//                "Expected status code 200"
//        );
//
//        // ✅ Allure evidence
//        AllureUtil.attachText("Request URL", "/api/getProjects");
//        AllureUtil.attachJson(
//                "Request Body",
//                "{ \"userId\": \"" + userId + "\", \"orgId\": \"1\" }"
//        );
//        AllureUtil.attachJson(
//                "Get Projects Response",
//                response.asPrettyString()
//        );
//
//        // 🔥 Stable extraction using JSONPath (BEST PRACTICE)
//        List<Integer> projectIds =
//                response.jsonPath().getList("results.projectId");
//
//        List<String> projectNames =
//                response.jsonPath().getList("results.projectName");
//
//        // ✅ Assertions
//        Assert.assertNotNull(projectIds, "Project IDs list should not be null");
//        Assert.assertTrue(projectIds.size() > 0, "Projects should exist");
//
//        // ✅ Extra safety check
//        Assert.assertEquals(
//                projectIds.size(),
//                projectNames.size(),
//                "Project IDs and Names count should match"
//        );
//
//        // ✅ Print projects
//        System.out.println("===== PROJECTS FOR USER " + userId + " =====");
//        for (int i = 0; i < projectIds.size(); i++) {
//            System.out.println(
//                    "Project ID: " + projectIds.get(i)
//                            + " | Project Name: " + projectNames.get(i)
//            );
//        }
//    }
//}
