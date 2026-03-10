package tests.generation;

import api.testCase.GetTestCaseSummaryForTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateATSGenerationPollingTest extends BaseTest {

    private static final int MAX_ATTEMPTS    = 15;
    private static final int POLL_INTERVAL_MS = 120000; // 2 minutes

    public void validateATSGenerationWithPolling() {

        if (!GeneratedTSStore.hasTS()) {
            throw new RuntimeException("❌ No TS available for ATS validation.");
        }
        if (!ATSStore.has()) {
            throw new RuntimeException(
                    "❌ No ATS-triggered TestCaseId found. Run GenerateATSTest first."
            );
        }

        Integer trackedTestCaseId = ATSStore.get();
        System.out.println("🎯 Validating ATS for TestCase → " + trackedTestCaseId);

        Report testData = JsonUtils.readJson(
                "testdata/testCase/getTestCaseSummaryForTS.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ Summary JSON missing or invalid.");
        }

        for (Integer tsId : GeneratedTSStore.getAll()) {

            Report.TestCase tc = new Report.TestCase(
                    testData.getTestCases().get(0)
            );

            Map<String, Object> request = new HashMap<>();
            request.put("testScenarioId", tsId);
            request.put("page", 1);
            request.put("pageSize", 200);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);
            tc.setTcId("POLL_ATS_TC_" + trackedTestCaseId + "_TS_" + tsId);
            tc.setName("Validate ATS Generation (Polling)");

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

                            System.out.println(
                                    "⏳ ATS Poll Attempt " + attempt + "/" + MAX_ATTEMPTS
                                            + " | TS → " + tsId
                            );

                            Response response = GetTestCaseSummaryForTSApi.getTestCaseSummary(
                                    request, tc.getRole(), tc.getAuthType()
                            );

                            // 🔍 Print full raw response for first attempt or on issues
                            if (attempt == 1) {
                                System.out.println("📋 Raw Response: " + response.asString());
                            }

                            List<Map<String, Object>> results =
                                    response.jsonPath().getList("results");

                            if (results == null || results.isEmpty()) {
                                System.out.println("⚠️ Empty results — ATS likely still processing...");
                                sleepOrThrow(attempt, MAX_ATTEMPTS,
                                        "❌ No test cases found after all attempts for TS " + tsId);
                                continue;
                            }

                            System.out.println("📦 Total results in response: " + results.size());

                            boolean found = false;

                            for (Map<String, Object> item : results) {
                                // FIX: Safe integer parsing
                                int testCaseId = toInt(item.get("testCaseId"));

                                if (testCaseId != trackedTestCaseId) {
                                    continue;
                                }

                                found = true;

                                // FIX: Use int (primitive) for comparison, not Integer
                                int    atsStatus = toInt(item.get("atsGenerationStatus"));
                                String atsText   = String.valueOf(item.get("atsGenerationStatusText")).trim();

                                System.out.println(
                                        "🔎 Found TC → " + testCaseId
                                                + " | atsGenerationStatus = " + atsStatus
                                                + " | atsGenerationStatusText = \"" + atsText + "\""
                                );

                                // ✅ SUCCESS — use primitive int comparison
                                if (atsStatus == 1 && "Generated".equalsIgnoreCase(atsText)) {
                                    System.out.println("✅ ATS Generation Completed → TC " + testCaseId);
                                    return response;
                                }

                                // ❌ FAILURE — ATS errored out, no point waiting
                                if (atsStatus == -1 || "Failed".equalsIgnoreCase(atsText)) {
                                    throw new RuntimeException(
                                            "❌ ATS Generation FAILED (not timeout) → TC "
                                                    + testCaseId + " | Status: " + atsStatus
                                                    + " | " + atsText
                                    );
                                }

                                // Still in progress
                                System.out.println(
                                        "⏳ ATS still processing → status=" + atsStatus
                                                + " text=\"" + atsText + "\""
                                );

                                break; // FIX: Found our TC, no need to check other items
                            }

                            if (!found) {
                                System.out.println(
                                        "⚠️ TC " + trackedTestCaseId
                                                + " not yet visible in TS → " + tsId
                                );
                            }

                            sleepOrThrow(attempt, MAX_ATTEMPTS,
                                    "❌ ATS Generation TIMEOUT → TC " + trackedTestCaseId
                                            + " | TS → " + tsId
                            );
                        }

                        // Should never reach here, but satisfies compiler
                        throw new RuntimeException(
                                "❌ ATS Generation TIMEOUT → TC " + trackedTestCaseId
                        );
                    }
            );
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    /** Safely converts Object → int regardless of whether it came back as
     *  Integer, Long, String, Double, etc. */
    private int toInt(Object value) {
        if (value == null) throw new RuntimeException("❌ Null value in toInt()");
        return Integer.parseInt(String.valueOf(value).trim());
    }

    /** Wait if there are remaining attempts, otherwise throw timeout. */
    private void sleepOrThrow(int attempt, int maxAttempts, String timeoutMsg) {
        if (attempt < maxAttempts) {
            try {
                System.out.println("⏱ Waiting 2 minutes before next poll...");
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("❌ Polling interrupted", e);
            }
        } else {
            throw new RuntimeException(timeoutMsg);
        }
    }
}