package tests.generation;

import api.generation.GenerateATSApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GenerateATSTest extends BaseTest {

    public void generateAtsApiTest() {

        // ===============================
        // 1️⃣ Validate prerequisites
        // ===============================

        if (!TestCaseStore.hasTestCases()) {
            throw new RuntimeException(
                    "❌ No TestCase IDs available. Run GetTestCaseSummaryForTS first."
            );
        }

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ Project ID is null. Project must be selected.");
        }

        String projectName = ProjectStore.getProjectName(projectId);
        if (projectName == null || projectName.isBlank()) {
            projectName = "Project_" + projectId;
            System.out.println("⚠️ Project Name missing in Store, using fallback: " + projectName);
        }

        // ===============================
        // 2️⃣ Resolve TestCaseId (CRITICAL CHANGE)
        // ===============================

        Integer testCaseId = TestCaseStore.getAnyTestCaseId();

        if (testCaseId == null) {
            throw new RuntimeException(
                    "❌ Unable to resolve TestCaseId from TestCaseStore."
            );
        }

        // ✅ Persist for polling validator
        ATSStore.set(testCaseId);

        System.out.println("🎯 ATS Triggered for TestCase → " + testCaseId);

        // ===============================
        // 3️⃣ Load JSON (Reporting Only)
        // ===============================

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/generation/generateATS.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ generateATS.json is missing or corrupt.");
        }

        // ===============================
        // 4️⃣ Execute ATS API
        // ===============================

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = (tc.getRequest() != null)
                    ? new HashMap<>((Map<String, Object>) tc.getRequest())
                    : new HashMap<>();

            // ✅ Inject TC-based payload
            request.put("sourceString", List.of(testCaseId));   // ✅ FIXED
            request.put("userId", TokenUtil.getUserId());
            request.put("projectId", projectId);
            request.put("projectName", projectName);

            if (request.get("automationFramework") == null) {
                System.out.println(
                        "ℹ️ 'automationFramework' missing. Injecting default: Playwright_Java"
                );
                request.put("automationFramework", "Playwright_Java");
            }

            tc.setRequest(request);

            System.out.println("🚀 Final ATS Payload → " + request);

            ApiTestExecutor.execute(
                    testData.getScenario() + " | " + projectName,
                    tc,
                    () -> {

                        Response response = GenerateATSApi.generateATS(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ ATS API returned null response.");
                        }

                        if (response.getStatusCode() != 200 &&
                                response.getStatusCode() != 201) {

                            throw new RuntimeException(
                                    "❌ ATS Generation failed! Status: " +
                                            response.getStatusCode() +
                                            " | Body: " + response.asString()
                            );
                        }

                        System.out.println(
                                "✅ ATS Trigger Accepted for TC → " + testCaseId
                        );

                        return response;
                    }
            );
        }

        System.out.println("✅ Step 29: Automation Code Generation Triggered Successfully.");
    }
}
