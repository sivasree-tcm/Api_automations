package tests.pipeline;

import api.project.GetTestCaseSummaryForTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ReportContext;
import report.ReportStep;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateExecutionPollingTest extends BaseTest {

    private static final int MAX_ATTEMPTS = 10;
    private static final int POLL_INTERVAL_MS = 120000; // 2 minutes

    public void validateExecutionState() {

        if (!GeneratedTSStore.hasTS()) {
            throw new RuntimeException("❌ No TestScenarioId available.");
        }

        if (!ATSStore.has()) {
            throw new RuntimeException("❌ No ATS TestCaseId available.");
        }

        Integer testScenarioId = GeneratedTSStore.getAll().get(0);
        Integer testCaseId = ATSStore.get();

        System.out.println("🎯 Polling Execution State");
        System.out.println("   TS → " + testScenarioId);
        System.out.println("   TC → " + testCaseId);

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/project/getTestCaseSummaryForTS.json",
                ConnectionReport.class
        );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(testData.getTestCases().get(0));

        /* ✅ Correct numeric payload */
        Map<String, Object> request = new HashMap<>();
        request.put("testScenarioId", testScenarioId);
        request.put("page", 1);
        request.put("pageSize", 10);
        request.put("userId", TokenUtil.getUserId());

        tc.setRequest(request);
        tc.setName("Execution Polling | TS → " + testScenarioId);

        ApiTestExecutor.execute(
                "ATS Execution Polling",
                tc,
                () -> {

                    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

                        System.out.println("⏳ Poll Attempt " + attempt);

                        Response response =
                                GetTestCaseSummaryForTSApi.getTestCaseSummary(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        List<Map<String, Object>> results =
                                response.jsonPath().getList("results");

                        if (results == null || results.isEmpty()) {
                            ReportContext.getTest().addStep(new ReportStep(
                                    "Fail",
                                    "Execution Result",
                                    "No test cases found for TS → " + testScenarioId
                            ));
                            return response; // stop polling safely
                        }

                        for (Map<String, Object> item : results) {

                            Integer returnedTcId =
                                    Integer.valueOf(String.valueOf(item.get("testCaseId")));

                            if (!returnedTcId.equals(testCaseId)) {
                                continue;
                            }

                            String executionState =
                                    String.valueOf(item.get("executionState"));

                            ReportContext.getTest().addStep(new ReportStep(
                                    "Info",
                                    "Execution State (Attempt " + attempt + ")",
                                    executionState
                            ));

                            /* ===============================
                               ✅ STOP IF PASSED
                               =============================== */
                            if ("passed".equalsIgnoreCase(executionState)) {

                                ReportContext.getTest().addStep(new ReportStep(
                                        "Pass",
                                        "Execution Result",
                                        "ATS Execution PASSED"
                                ));

                                System.out.println("✅ Execution PASSED");
                                return response;
                            }

                            /* ===============================
                               ✅ STOP IF FAILED
                               =============================== */
                            else if ("failed".equalsIgnoreCase(executionState)) {

                                ReportContext.getTest().addStep(new ReportStep(
                                        "Fail",
                                        "Execution Result",
                                        "ATS Execution FAILED"
                                ));

                                System.out.println("❌ Execution FAILED");
                                return response; // ✅ graceful stop (NO exception)
                            }

                            /* ===============================
                               ⏳ STILL RUNNING
                               =============================== */
                            else {
                                System.out.println("⌛ Execution still running...");
                            }
                        }

                        /* ✅ Wait before next poll */
                        if (attempt < MAX_ATTEMPTS) {
                            try {
                                System.out.println("⏱ Waiting 2 minutes...");
                                Thread.sleep(POLL_INTERVAL_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("❌ Polling interrupted", e);
                            }
                        }
                    }

                    ReportContext.getTest().addStep(new ReportStep(
                            "Fail",
                            "Execution Result",
                            "Polling TIMEOUT. Execution did not complete."
                    ));

                    return null;
                }
        );
    }
}