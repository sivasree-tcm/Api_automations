package tests.video;

import api.video.GetAutomationVideoApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class GetAutomationVideoTest extends BaseTest {

    public void getAutomationVideo() {

        /* ===============================
           1️⃣ Resolve Dynamic Values
           =============================== */

        Integer projectId = ProjectStore.getSelectedProjectId();
        String projectName = ProjectStore.getProjectName(projectId);
        String framework = ProjectStore.getAutomationFramework();
        String testCaseNumber = TestCaseStore.getAnyTestCaseNumber();
        Integer userId = TokenUtil.getUserId();

        if (projectId == null ||
                projectName == null ||
                framework == null ||
                testCaseNumber == null ||
                userId == null) {

            throw new RuntimeException("❌ Required values missing from Stores.");
        }

        // Normalize framework for API
        String normalizedFramework =
                framework.toLowerCase().contains("playwright")
                        ? "playwright"
                        : framework.toLowerCase();

        /* ===============================
           2️⃣ Load JSON
           =============================== */

        Report testData = JsonUtils.readJson(
                "testdata/video/getAutomationVideo.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getAutomationVideo.json missing or invalid.");
        }

        /* ===============================
           3️⃣ Execute API
           =============================== */

        for (Report.TestCase tc : testData.getTestCases()) {

            /* ✅ Build Payload BEFORE execution */
            Map<String, Object> request = new HashMap<>();

            request.put("userId", userId);
            request.put("projectId", projectId);
            request.put("testCaseNumber", testCaseNumber);
            request.put("framework", normalizedFramework);

            /* ✅ Attach payload to report */
            tc.setRequest(request);

            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🎥 Get Automation Video API");
            System.out.println("📁 Project   : " + projectName + " (ID: " + projectId + ")");
            System.out.println("🧪 Test Case : " + testCaseNumber);
            System.out.println("⚙ Framework  : " + normalizedFramework);
            System.out.println("📦 Payload   : " + request);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            ApiTestExecutor.execute(
                    testData.getScenario() + " | " + projectName,
                    tc,
                    () -> {

                        Response response =
                                GetAutomationVideoApi.getAutomationVideo(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Get Automation Video Failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        Boolean success = response.jsonPath().getBoolean("success");
                        String videoUrl = response.jsonPath().getString("videoUrl");
                        String downloadUrl = response.jsonPath().getString("downloadUrl");
                        String fileName = response.jsonPath().getString("fileName");

                        if (success == null || !success) {
                            throw new RuntimeException("❌ success flag is false.");
                        }

                        if (videoUrl == null || videoUrl.isBlank()) {
                            throw new RuntimeException("❌ videoUrl missing in response.");
                        }

                        if (fileName == null || !fileName.contains(testCaseNumber)) {
                            throw new RuntimeException("❌ fileName mismatch in response.");
                        }

                        System.out.println("🎥 Video URL → " + videoUrl);
                        System.out.println("⬇ Download URL → " + downloadUrl);
                        System.out.println("📄 File Name → " + fileName);

                        return response;
                    }
            );
        }
    }
}