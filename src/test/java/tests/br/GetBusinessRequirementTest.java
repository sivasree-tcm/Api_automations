package tests.br;

import api.br.GetBRByProjectApi;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetBusinessRequirementTest {

    public void fetchBRsForAllProjects() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/br/getBRByProject.json",
                        ConnectionReport.class
                );

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            Map<String, Object> request = new HashMap<>();
            request.put("projectId", projectId);
            request.put("page", 1);
            request.put("pageSize", 10);
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("GET_BR_" + projectId);
            tc.setName("Get BR | Project " + projectId);
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetBRByProjectApi.getBRs(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        List<Map<String, Object>> brList =
                                response.jsonPath().getList("data");

                        // ✅ No BR → skip
                        if (brList == null || brList.isEmpty()) {
                            System.out.println(
                                    "ℹ No BR found for project " + projectId
                            );
                            return response;
                        }

                        List<Integer> brIds = new ArrayList<>();
                        for (Map<String, Object> br : brList) {
                            brIds.add((Integer) br.get("brId"));
                        }

                        BusinessRequirementStore.store(projectId, brIds);

                        System.out.println(
                                "✅ BR IDs stored → project=" + projectId +
                                        " | brIds=" + brIds
                        );

                        return response;
                    }
            );
        }
    }
    public void fetchBRsForProject() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ Project ID not found. Run GetProjects first.");
        }

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/br/getBRByProject.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_BR_" + projectId);
        tc.setName("Get BR | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    int page = 1;
                    List<Integer> allBrIds = new ArrayList<>();
                    Response lastResponse = null;

                    while (true) {

                        Map<String, Object> request = new HashMap<>();
                        request.put("projectId", projectId);
                        request.put("page", page);
                        request.put("pageSize", 10);
                        request.put("userId", TokenUtil.getUserId());

                        Response response =
                                GetBRByProjectApi.getBRs(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        lastResponse = response; // ✅ always keep latest response

                        List<Map<String, Object>> brList =
                                response.jsonPath().getList("data");

                        // ✅ Stop pagination
                        if (brList == null || brList.isEmpty()) {
                            System.out.println("ℹ No more BRs found. Stopping pagination.");
                            break;
                        }

                        for (Map<String, Object> br : brList) {
                            allBrIds.add((Integer) br.get("brId"));
                        }

                        System.out.println(
                                "✅ Page " + page + " fetched → BRs: " + brList.size()
                        );

                        page++;
                    }

                    // ✅ Store only once
                    BusinessRequirementStore.store(projectId, allBrIds);

                    System.out.println(
                            "✅ Total BRs stored for project " + projectId +
                                    " = " + allBrIds.size()
                    );

                    // ✅ MUST return response (never null)
                    return lastResponse;
                }
        );
    }
}
