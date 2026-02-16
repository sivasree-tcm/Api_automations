package tests.project;

import api.project.GetTestCaseSummaryForTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.GeneratedTSStore;
import utils.JsonUtils;
import utils.TestCaseStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTestCaseSummaryForTSTest extends BaseTest {

    public void getTestCaseSummaryForTS() {

        if (!GeneratedTSStore.hasTS()) {
            throw new RuntimeException(
                    "❌ No TS available. Run TS → TC generation first."
            );
        }

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getTestCaseSummaryForTS.json",
                        ConnectionReport.class
                );

        for (Integer tsId : GeneratedTSStore.getAll()) {

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            Map<String, Object> request = new HashMap<>();
            request.put("testScenarioId", tsId);
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            tc.setRequest(request);
            tc.setTcId("GET_TC_SUMMARY_TS_" + tsId);
            tc.setName("Get Test Case Summary | TS " + tsId);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

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

                        for (Map<String, Object> item : results) {
                            Integer tcId =
                                    Integer.valueOf(
                                            String.valueOf(item.get("testCaseId"))
                                    );
                            TestCaseStore.add(tcId);
                        }

                        System.out.println(
                                "📦 Stored TestCase IDs for TS " + tsId +
                                        " → " + results.size()
                        );

                        return response;
                    }
            );
        }
    }
}