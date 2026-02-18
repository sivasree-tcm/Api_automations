package tests.generation;

import api.generation.GetTSByBRApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GetTSByBRTest extends BaseTest {


    public void getTestScenariosForGeneratedBRs() {

        if(!GeneratedBRStore.hasBrs() ){
            throw new RuntimeException("❌ No BRs available");
        }

        var testData = JsonUtils.readJson(
                "testdata/generation/getTSByBR.json",
                tests.connection.ConnectionReport.class
        );

        for (Integer brId : GeneratedBRStore.getBrIds()) {

            int page = 1;
            int pageSize = 10;
            AtomicInteger totalPages = new AtomicInteger(1); // will update after first call

            do {
                Map<String, Object> request = new HashMap<>();
                request.put("brId", brId);
                request.put("page", page);
                request.put("pageSize", pageSize);
                request.put("userId", TokenUtil.getUserId());

                var tc = new tests.connection.ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

                tc.setRequest(request);
                tc.setTcId("GET_TS_BR_" + brId + "_PAGE_" + page);
                tc.setName("Get TS for BR " + brId + " | Page " + page);

                int currentPage = page;

                ApiTestExecutor.execute(
                        "Get TS for BR " + brId + " | Page " + currentPage,
                        tc,
                        () -> {

                            Response response =
                                    GetTSByBRApi.getTSByBrId(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            if (response.getStatusCode() == 200) {

                                Integer totalCount =
                                        response.jsonPath().getInt("totalCount");

                                if (totalCount != null) {
                                    totalPages.set((int) Math.ceil(
                                            (double) totalCount / pageSize
                                    ));
                                }

                                List<Map<String, Object>> results =
                                        response.jsonPath().getList("results");

                                if (results != null && !results.isEmpty()) {
                                    for (Map<String, Object> ts : results) {
                                        Integer tsId =
                                                ((Number) ts.get("testScenarioId")).intValue();
                                        TestScenarioStore.addTs(brId, tsId);
                                    }
                                }

                                System.out.println(
                                        "✅ BR " + brId +
                                                " | Page " + currentPage +
                                                " | Fetched " +
                                                (results == null ? 0 : results.size()) +
                                                " TS"
                                );
                            }

                            return response;
                        }
                );

                page++;

            } while (page <= totalPages.get());
        }
    }
}
