//package tests.project;
//
//import api.project.ProjectApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import models.project.ProjectRequest;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import report.CustomReportManager;
//import utils.JsonUtils;
//import utils.TokenUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class ProjectTest extends BaseTest {
//
//    @Test
//    public void createAndVerifyProjectTest() {
//
//        String testName = "createAndVerifyProjectTest";
//        String status = "PASS";
//        long startTime = System.currentTimeMillis();
//
//        List<Map<String, String>> steps = new ArrayList<>();
//
//        try {
//            // 🔹 Token / User
//            int userId = TokenUtil.getUserId();
//            steps.add(CustomReportManager.step(
//                    "Info", "User ID", String.valueOf(userId)
//            ));
//
//            // 🔹 Load request
//            ProjectRequest request = JsonUtils.readJson(
//                    "testdata/project/createProject.json",
//                    ProjectRequest.class
//            );
//
//            request.setUserId(String.valueOf(userId));
//            request.setProjectCreatedBy(String.valueOf(userId));
//            request.setProjectName("Automation_" + System.currentTimeMillis());
//
//            steps.add(CustomReportManager.step(
//                    "Info", "Request Payload",
//                    JsonUtils.toJson(request)
//            ));
//
//            // 🔹 API Call
//            Response response = ProjectApi.createProject(request);
//
//            steps.add(CustomReportManager.step(
//                    "Info", "HTTP Status",
//                    String.valueOf(response.getStatusCode())
//            ));
//
//            steps.add(CustomReportManager.step(
//                    "Info", "Response",
//                    response.asPrettyString()
//            ));
//
//            // 🔹 Validation
//            Assert.assertEquals(response.getStatusCode(), 200);
//            steps.add(CustomReportManager.step(
//                    "Pass", "Result", "Project created successfully"
//            ));
//
//        } catch (AssertionError | Exception e) {
//            status = "FAIL";
//
//            steps.add(CustomReportManager.step(
//                    "Fail", "Error",
//                    e.getMessage()
//            ));
//
//            throw e;
//
//        } finally {
//            long duration = System.currentTimeMillis() - startTime;
//
//            // ✅ WRITE TO JSON REPORT (ALWAYS)
//            CustomReportManager.addTestResult(
//                    testName,
//                    status,
//                    duration,
//                    steps
//            );
//        }
//    }
//}
