package tests.generation;

import api.project.GetTestCaseSummaryForTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateATSGenerationPollingTest extends BaseTest {

    private static final int MAX_ATTEMPTS = 10;
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

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getTestCaseSummaryForTS.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ Summary JSON missing or invalid.");
        }

        for (Integer tsId : GeneratedTSStore.getAll()) {

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            Map<String, Object> request = new HashMap<>();
            request.put("testScenarioId", tsId);
            request.put("page", 1);
            request.put("pageSize", 50);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);
            tc.setTcId("POLL_ATS_TC_" + trackedTestCaseId + "_TS_" + tsId);
            tc.setName("Validate ATS Generation (Polling) | TC " +
                    trackedTestCaseId + " | TS " + tsId);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

                            System.out.println(
                                    "⏳ ATS Poll Attempt " + attempt +
                                            " / " + MAX_ATTEMPTS +
                                            " | TS → " + tsId
                            );

                            Response response =
                                    GetTestCaseSummaryForTSApi.getTestCaseSummary(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            List<Map<String, Object>> results =
                                    response.jsonPath().getList("results");

                            if (results == null || results.isEmpty()) {
                                throw new RuntimeException(
                                        "❌ No test cases returned for TS " + tsId
                                );
                            }

                            boolean testCaseFound = false;

                            for (Map<String, Object> item : results) {

                                Integer testCaseId = Integer.valueOf(
                                        String.valueOf(item.get("testCaseId"))
                                );

                                if (!testCaseId.equals(trackedTestCaseId)) {
                                    continue;
                                }

                                testCaseFound = true;

                                Integer atsStatus = Integer.valueOf(
                                        String.valueOf(item.get("atsGenerationStatus"))
                                );

                                String atsText = String.valueOf(
                                        item.get("atsGenerationStatusText")
                                );

                                System.out.println(
                                        "🔎 Tracking TC → " + testCaseId +
                                                " | ATS → " + atsStatus +
                                                " | " + atsText
                                );

                                if (atsStatus == 1 &&
                                        "Generated".equalsIgnoreCase(atsText)) {

                                    System.out.println(
                                            "✅ ATS Generation Completed → TC " + testCaseId
                                    );

                                    return response;
                                }
                            }

                            if (!testCaseFound) {
                                System.out.println(
                                        "⚠️ Tracked TestCase not part of TS → " + tsId
                                );
                            }

                            if (attempt < MAX_ATTEMPTS) {
                                try {
                                    System.out.println("⏱ Waiting 2 minutes before next poll...");
                                    Thread.sleep(POLL_INTERVAL_MS);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException("❌ Polling interrupted", e);
                                }
                            }
                        }

                        throw new RuntimeException(
                                "❌ ATS Generation TIMEOUT → TC " +
                                        trackedTestCaseId +
                                        " | TS → " + tsId
                        );
                    }
            );
        }
    }
}
