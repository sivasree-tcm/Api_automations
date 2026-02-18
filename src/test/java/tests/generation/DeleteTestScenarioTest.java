package tests.generation;

import api.generation.DeleteTestScenarioApi;
import api.generation.GetTSByBRApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class DeleteTestScenarioTest extends BaseTest {


    public void deleteLastTestScenario() {

        // 1️⃣ Load test data (MANDATORY for ApiTestExecutor)
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/deleteTS.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases().isEmpty()) {
            throw new RuntimeException("❌ deleteTestScenario.json missing");
        }

        // 2️⃣ Create TestCase object (THIS FIXES NPE)
        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(testData.getTestCases().get(0));

        if (!GeneratedBRStore.hasBrs()) {
            throw new RuntimeException("❌ No BRs available");
        }

        Integer brId = GeneratedBRStore.getBrIds()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("❌ No BR found")
                );

        int page = 1;
        int pageSize = 10;
        int totalPages = 1;
        Integer lastTsId = null;

        do {
            Map<String, Object> fetchRequest = new HashMap<>();
            fetchRequest.put("brId", brId);
            fetchRequest.put("page", page);
            fetchRequest.put("pageSize", pageSize);
            fetchRequest.put("userId", TokenUtil.getUserId());

            Response response =
                    GetTSByBRApi.getTSByBrId(
                            fetchRequest,
                            tc.getRole(),
                            tc.getAuthType()
                    );

            Integer totalCount = response.jsonPath().getInt("totalCount");
            if (totalCount != null) {
                totalPages =
                        (int) Math.ceil((double) totalCount / pageSize);
            }

            List<Map<String, Object>> results =
                    response.jsonPath().getList("results");

            if (page == totalPages && results != null && !results.isEmpty()) {
                lastTsId =
                        ((Number) results.get(results.size() - 1)
                                .get("testScenarioId"))
                                .intValue();
            }

            page++;
        } while (page <= totalPages);

        if (lastTsId == null) {
            throw new RuntimeException("❌ No TestScenario found to delete");
        }

        // 3️⃣ Prepare delete request
        Map<String, Object> deleteRequest = new HashMap<>();
        deleteRequest.put("testScenarioId", List.of(lastTsId));
        deleteRequest.put("userId", TokenUtil.getUserId());

        tc.setRequest(deleteRequest);
        tc.setTcId("DELETE_TS_" + lastTsId);
        tc.setName("Delete last Test Scenario from pagination");

        // 4️⃣ Execute with reporting ✅
        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> DeleteTestScenarioApi.deleteTestScenario(
                        deleteRequest,
                        tc.getRole()
                )
        );
    }
}
