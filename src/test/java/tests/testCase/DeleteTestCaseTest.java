package tests.testCase;

import api.testCase.DeleteTestCaseApi;
import api.testCase.GetTestCaseSummaryForTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.*;

public class DeleteTestCaseTest extends BaseTest {


    public void deleteLastTestCase() {

        Integer tsId = GeneratedTSStore.getAnyTsId();
        if (tsId == null) {
            throw new RuntimeException("❌ No TS available to fetch Test Cases");
        }

        Report testData = JsonUtils.readJson(
                "testdata/testCase/deleteTestCase.json",
                Report.class
        );

        Report.TestCase tc =
                new Report.TestCase(testData.getTestCases().get(0));

        int page = 1;
        int pageSize = 10;
        int totalPages = 1;
        Integer lastTcId = null;

        do {
            Map<String, Object> summaryRequest = new HashMap<>();
            summaryRequest.put("testScenarioId", tsId);
            summaryRequest.put("page", page);
            summaryRequest.put("pageSize", pageSize);
            summaryRequest.put("userId", TokenUtil.getUserId(tc.getRole()));

            Response response =
                    GetTestCaseSummaryForTSApi.getTestCaseSummary(
                            summaryRequest,
                            tc.getRole(),
                            tc.getAuthType()
                    );

            if (response.getStatusCode() != 200) {
                throw new RuntimeException("❌ Failed to fetch Test Case summary");
            }

            Integer totalCount = response.jsonPath().getInt("totalCount");
            if (totalCount != null) {
                totalPages = (int) Math.ceil((double) totalCount / pageSize);
            }

            List<Map<String, Object>> results =
                    response.jsonPath().getList("results");

            if (page == totalPages && results != null && !results.isEmpty()) {
                Map<String, Object> lastTc =
                        results.get(results.size() - 1);
                lastTcId =
                        ((Number) lastTc.get("testCaseId")).intValue();
            }

            page++;

        } while (page <= totalPages);

        if (lastTcId == null) {
            throw new RuntimeException("❌ No Test Case found to delete");
        }

        System.out.println("🗑 Deleting Test Case ID → " + lastTcId);

        Map<String, Object> deleteRequest = new HashMap<>();
        deleteRequest.put("testCaseId", List.of(lastTcId));
        deleteRequest.put("userId", TokenUtil.getUserId(tc.getRole()));

        tc.setRequest(deleteRequest);
        tc.setName("Delete Test Case ID " + lastTcId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> DeleteTestCaseApi.deleteTestCase(
                        deleteRequest,
                        tc.getRole(),
                        tc.getAuthType()
                )
        );
    }
}
