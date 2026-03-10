package tests.connection;

import api.pipeline.ExecuteTestsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class ExecuteTestsTest extends BaseTest {

    // ✅ Confirmed working pipeline values from UI
    private static final Integer WORKING_PIPELINE_ID   = 147;
    private static final String  WORKING_PIPELINE_NAME = "ATS_UI_Automation_Pipeline";
    private static final Integer WORKING_CONNECTION_ID  = 33;


    public void executeAutomationTests() {

        // 1️⃣ Load JSON Data
        Report testData = JsonUtils.readJson(
                "testdata/pipeline/executeTests.json",
                Report.class
        );

        if (testData == null || testData.getTestCases().isEmpty()) {
            throw new RuntimeException("❌ executeTests.json missing or empty");
        }

        Report.TestCase tc = new Report.TestCase(
                testData.getTestCases().get(0)
        );

        // 2️⃣ Resolve Project Info
        Integer projectId   = ProjectStore.getProjectId();
        String  projectName = ProjectStore.getProjectName(projectId);
        Integer userId      = TokenUtil.getUserId();

        // 3️⃣ Resolve Test Case Data
        Integer dynamicTcId    = TestCaseStore.getAnyTestCaseId();
        String  dynamicTcNumber = TestCaseStore.getAnyTestCaseNumber();
        Integer dynamicTsId    = GeneratedTSStore.getAnyTsId();

        // 4️⃣ Validate all required values
        if (projectName == null || projectName.isEmpty()) {
            throw new RuntimeException("❌ projectName is null — check ProjectStore");
        }
        if (dynamicTcId == null || dynamicTcNumber == null || dynamicTsId == null) {
            throw new RuntimeException("❌ Test case data null — check TestCaseStore / GeneratedTSStore");
        }

        // 5️⃣ Build selectedData
        Map<String, Object> tcDetails = new HashMap<>();
        tcDetails.put("tcId",     dynamicTcId);
        tcDetails.put("tcNumber", dynamicTcNumber);
        tcDetails.put("refTSId",  dynamicTsId);

        List<Map<String, Object>> selectedData = new ArrayList<>();
        selectedData.add(tcDetails);

        // 6️⃣ Build Payload — using confirmed working pipeline values
        Map<String, Object> request = new HashMap<>();
        request.put("projectName",  projectName);
        request.put("userId",       userId);
        request.put("refProjectId", projectId);
        request.put("connectionId", WORKING_CONNECTION_ID);  // ✅ 33
        request.put("pipelineId",   WORKING_PIPELINE_ID);    // ✅ 147
        request.put("pipelineName", WORKING_PIPELINE_NAME);  // ✅ ATS_UI_Automation_Pipeline
        request.put("browser",      "chromium");
        request.put("headless",     "true");
        request.put("azureRef",     "refs/heads/main");
        request.put("selectedData", selectedData);

        // 7️⃣ Sync Metadata
        tc.setRequest(request);
        tc.setName("Dynamic Pipeline Execution - " + dynamicTcNumber);

        // 8️⃣ Log Before Execution
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚀 Triggering Pipeline Execution...");
        System.out.println("📁 Project    : " + projectName + " (ID: " + projectId + ")");
        System.out.println("🔗 Connection : " + WORKING_CONNECTION_ID);
        System.out.println("⚙️ Pipeline   : " + WORKING_PIPELINE_NAME + " (ID: " + WORKING_PIPELINE_ID + ")");
        System.out.println("🧪 Test Case  : " + dynamicTcNumber + " (tcId: " + dynamicTcId + ")");
        System.out.println("📦 Payload    : " + request);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // 9️⃣ Execute
        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {
                    Response response = ExecuteTestsApi.executeTests(request, tc.getRole());

                    if (response == null) {
                        throw new RuntimeException("❌ API returned null response");
                    }

                    if (response.getStatusCode() == 200) {
                        System.out.println("✅ Build Started Successfully!");
                        System.out.println("🆔 Run ID   : " + response.jsonPath().get("runId"));
                        System.out.println("🔗 Azure URL: " + response.jsonPath().get("webUrl"));
                        System.out.println("📌 State    : " + response.jsonPath().get("state"));
                    } else {
                        System.err.println("❌ Execution Failed!");
                        System.err.println("📛 Status   : " + response.getStatusCode());
                        System.err.println("📛 Error    : " + response.getBody().asString());
                    }

                    return response;
                }
        );
    }
}