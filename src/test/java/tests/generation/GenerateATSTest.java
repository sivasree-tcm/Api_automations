package tests.generation;

import api.generation.GenerateATSApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GenerateATSTest extends BaseTest {


    public void generateAtsApiTest() {

        // ===============================
        // 1️⃣ Validate prerequisites (Dynamic Data)
        // ===============================
        if (!GeneratedTSStore.hasTS()) {
            throw new RuntimeException("❌ No Test Scenarios available in GeneratedTSStore.");
        }

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ Project ID is null. Project must be selected.");
        }

        String projectName = ProjectStore.getProjectName(projectId);
        if (projectName == null || projectName.isBlank()) {
            // Fallback for development if store is cleared
            projectName = "Project_" + projectId;
            System.out.println("⚠️ Project Name missing in Store, using fallback: " + projectName);
        }

        // ===============================
        // 2️⃣ Load JSON (Structure for Reporting)
        // ===============================
        ConnectionReport testData = JsonUtils.readJson(
                "testdata/generation/generateATS.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ generateATS.json is missing or corrupt.");
        }

        // ===============================
        // 3️⃣ Execute for each test case
        // ===============================
        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            // Initialize request map safely
            Map<String, Object> request = (tc.getRequest() != null)
                    ? new HashMap<>((Map<String, Object>) tc.getRequest())
                    : new HashMap<>();

            // 🔹 Get first generated TS dynamically
            Integer tsId = GeneratedTSStore.getAll().stream().findFirst().orElse(null);
            if (tsId == null) {
                throw new RuntimeException("❌ TS ID missing from GeneratedTSStore");
            }

            // 🔹 Inject Dynamic Values (Mandatory for API)
            request.put("sourceString", List.of(tsId));
            request.put("userId", TokenUtil.getUserId()); // Use current logged in User
            request.put("projectId", projectId);
            request.put("projectName", projectName);

            // 🔹 Framework Logic (Handles the RuntimeException)
            if (request.get("automationFramework") == null) {
                System.out.println("ℹ️ 'automationFramework' not in JSON. Injecting default: Playwright_Java");
                request.put("automationFramework", "Playwright_Java");
            }

            // Update the TestCase object with the full payload for reporting
            tc.setRequest(request);

            // 🔍 Debugging log for the deployed environment
            System.out.println("🚀 Final ATS Payload → " + request);

            // ===============================
            // 4️⃣ Execute API via Reporter
            // ===============================
            ApiTestExecutor.execute(
                    testData.getScenario() + " | " + projectName,
                    tc,
                    () -> {
                        Response response = GenerateATSApi.generateATS(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
                            throw new RuntimeException(
                                    "❌ ATS Generation failed! Status: " + response.getStatusCode() +
                                            " | Body: " + response.asString()
                            );
                        }
                        return response;
                    }
            );
        }
        System.out.println("✅ Step 29: Automation Code Generation Triggered Successfully.");
    }
}
//package tests.generation;
//
//import api.generation.GenerateATSApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.annotations.Test;
//import tests.connection.ConnectionReport;
//import tests.user.ApiTestExecutor;
//import utils.JsonUtils;
//
//import java.util.*;
//
//public class GenerateATSTest extends BaseTest {
//
//    @Test
//    public void generateAtsApiTest() {
//
//        // ===============================
//        // 🔴 HARD-CODED VALUES (TEMP)
//        // ===============================
//
//        Integer HARD_TS_ID = 37182;
//        Integer HARD_USER_ID = 33;
//        Integer HARD_PROJECT_ID = 1671;
//        String HARD_PROJECT_NAME = "DuplicateTest_1770823991797";
//        String HARD_AUTOMATION_FRAMEWORK =
//                "Playwright_Java, Appium_Java";
//
//        // ===============================
//        // Load JSON ONLY for reporting
//        // ===============================
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/generation/generateATS.json",
//                        ConnectionReport.class
//                );
//
//        if (testData == null || testData.getTestCases() == null) {
//            throw new RuntimeException("❌ generateATS.json missing");
//        }
//
//        for (ConnectionReport.TestCase tc : testData.getTestCases()) {
//
//            Map<String, Object> request = new HashMap<>();
//
//            // 🔴 FORCE HARD VALUES
//            request.put("sourceString", List.of(HARD_TS_ID));
//            request.put("userId", HARD_USER_ID);
//            request.put("projectId", HARD_PROJECT_ID);
//            request.put("projectName", HARD_PROJECT_NAME);
//            request.put("automationFramework", HARD_AUTOMATION_FRAMEWORK);
//
//            tc.setRequest(request);
//            tc.setTcId("GATS_HARDCODED");
//            tc.setName("Generate ATS – Hardcoded Values");
//
//            System.out.println("🚀 ATS REQUEST (HARDCODED) → " + request);
//
//            ApiTestExecutor.execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> {
//                        Response response =
//                                GenerateATSApi.generateATS(
//                                        request,
//                                        tc.getRole(),
//                                        tc.getAuthType()
//                                );
//
//                        if (response.getStatusCode() != 200) {
//                            throw new RuntimeException(
//                                    "❌ ATS generation failed. Status → "
//                                            + response.getStatusCode()
//                            );
//                        }
//
//                        return response;
//                    }
//            );
//        }
//    }
//}
