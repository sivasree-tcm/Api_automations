package tests.generation;

import api.generation.GenerateATSApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GenerateATSTest extends BaseTest {

    public void generateAtsApiTest() {

        /* ===============================
           1️⃣ Validate prerequisites
           =============================== */

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

        /* ===============================
           2️⃣ Resolve Automation Framework (CRITICAL FIX)
           =============================== */

        String automationFramework = ProjectStore.getAutomationFramework();

        if (automationFramework == null || automationFramework.isBlank()) {
            throw new RuntimeException(
                    "❌ Automation Framework not set in ProjectStore. " +
                            "Ensure GetProjectDetailsTest executed successfully."
            );
        }

        System.out.println("✅ Using Automation Framework from Store → " + automationFramework);

        /* ===============================
           3️⃣ Resolve TestCaseId
           =============================== */

        Integer testCaseId = TestCaseStore.getAnyTestCaseId();

        if (testCaseId == null) {
            throw new RuntimeException(
                    "❌ Unable to resolve TestCaseId from TestCaseStore."
            );
        }

        ATSStore.set(testCaseId);

        System.out.println("🎯 ATS Triggered for TestCase → " + testCaseId);

        /* ===============================
           4️⃣ Load JSON (Reporting Only)
           =============================== */

        Report testData = JsonUtils.readJson(
                "testdata/generation/generateATS.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ generateATS.json is missing or corrupt.");
        }

        /* ===============================
           5️⃣ Execute ATS API
           =============================== */

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            request.put("sourceString", List.of(testCaseId));     // ✅ TC based
            request.put("userId", TokenUtil.getUserId());
            request.put("projectId", projectId);
            request.put("projectName", projectName);
            request.put("automationFramework", automationFramework); // ✅ STRICT STORE VALUE

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

        System.out.println("✅ Step: Automation Code Generation Triggered Successfully.");
    }
}