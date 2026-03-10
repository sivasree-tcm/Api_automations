package tests.project;

import api.generation.GetGenerationStatusApi;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetGenerationStatusTest {

    // =========================
    // BR → TS STATUS CHECK
    // =========================
    public void waitUntilAllCompleted() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ No project selected");
        }

        List<Integer> brIds =
                BusinessRequirementStore.getGeneratedBRs(projectId);

        if (brIds == null || brIds.isEmpty()) {
            throw new RuntimeException("❌ No BRs found to check generation status");
        }

        System.out.println("🧪 BRs used for generation status → " + brIds);

        Report testData =
                JsonUtils.readJson(
                        "testdata/generation/getGenerationStatus.json",
                        Report.class
                );

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_GEN_STATUS_BR_" + projectId);
        tc.setName("Wait for TS Generation Completion | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    List<Integer> pending = new ArrayList<>(brIds);
                    List<Integer> completed = new ArrayList<>();

                    while (!pending.isEmpty()) {

                        List<Integer> stillPending = new ArrayList<>();

                        for (Integer brId : pending) {

                            Map<String, Object> request = new HashMap<>();
                            request.put("projectId", projectId);
                            request.put("source", "BR");
                            request.put("userId", TokenUtil.getUserId());
                            request.put("pending", List.of(brId));

                            Response response =
                                    GetGenerationStatusApi.getStatus(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            boolean isCompleted =
                                    isCompleted(response, brId);

                            if (isCompleted) {
                                completed.add(brId);
                            } else {
                                stillPending.add(brId);
                            }
                        }

                        System.out.println("✅ Completed BRs → " + completed);
                        System.out.println("⏳ Pending BRs → " + stillPending);

                        pending = stillPending;

                        if (!pending.isEmpty()) {
                            sleep();
                        }
                    }

                    BusinessRequirementStore.storeCompletedBRs(
                            projectId,
                            completed
                    );

                    System.out.println(
                            "🎉 ALL BRs completed for project " +
                                    projectId + " → " + completed
                    );

                    return responseForReport(projectId, "BR", completed, tc);
                }
        );
    }

    // =========================
    // TS → TC STATUS CHECK
    // =========================
    public void waitUntilAllCompletedForTC() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        if (projectId == null) {
            throw new RuntimeException("❌ No project selected");
        }

        List<Integer> tsIds = GeneratedTSStore.getAll();

        if (tsIds == null || tsIds.isEmpty()) {
            throw new RuntimeException("❌ No TS available for TC generation status check");
        }

        System.out.println("🧪 TS used for TC generation status → " + tsIds);

        Report testData =
                JsonUtils.readJson(
                        "testdata/generation/getGenerationStatus.json",
                        Report.class
                );

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_GEN_STATUS_TC_" + projectId);
        tc.setName("Wait for TC Generation Completion | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    List<Integer> pending = new ArrayList<>(tsIds);
                    List<Integer> completed = new ArrayList<>();

                    while (!pending.isEmpty()) {

                        List<Integer> stillPending = new ArrayList<>();

                        for (Integer tsId : pending) {

                            Map<String, Object> request = new HashMap<>();
                            request.put("projectId", projectId);
                            request.put("source", "TS");
                            request.put("userId", TokenUtil.getUserId());
                            request.put("pending", List.of(tsId));

                            Response response =
                                    GetGenerationStatusApi.getStatus(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            boolean isCompleted =
                                    isCompleted(response, tsId);

                            if (isCompleted) {
                                completed.add(tsId);
                            } else {
                                stillPending.add(tsId);
                            }
                        }

                        System.out.println("✅ Completed TS → " + completed);
                        System.out.println("⏳ Pending TS → " + stillPending);

                        pending = stillPending;

                        if (!pending.isEmpty()) {
                            sleep();
                        }
                    }

                    System.out.println(
                            "🎉 ALL TS completed for project " +
                                    projectId + " → " + completed
                    );

                    return responseForReport(projectId, "TS", completed, tc);
                }
        );
    }

    // =========================
    // 🔧 HELPER METHODS
    // =========================

    private boolean isCompleted(Response response, Integer id) {

        Object body = response.jsonPath().get("$");

        if (body instanceof List) {
            List<Map<String, Object>> list =
                    response.jsonPath().getList("$");

            if (list == null || list.isEmpty()) return false;

            for (Map<String, Object> item : list) {
                Integer responseId = (Integer) item.get("brId");
                if (responseId == null) {
                    responseId = (Integer) item.get("tsId");
                }
                if (id.equals(responseId)) {
                    String status = String.valueOf(item.get("status"));
                    return "Completed".equalsIgnoreCase(status);
                }
            }
        }

        if (body instanceof Map) {
            Map<String, Object> map =
                    response.jsonPath().getMap("$");

            String status = String.valueOf(map.get("status"));
            return "Completed".equalsIgnoreCase(status);
        }

        return false;
    }

    private Response responseForReport(
            Integer projectId,
            String source,
            List<Integer> completed,
            Report.TestCase tc
    ) {
        Map<String, Object> finalRequest = new HashMap<>();
        finalRequest.put("projectId", projectId);
        finalRequest.put("source", source);
        finalRequest.put("userId", TokenUtil.getUserId());
        finalRequest.put("pending", completed);

        return GetGenerationStatusApi.getStatus(
                finalRequest,
                tc.getRole(),
                tc.getAuthType()
        );
    }

    private void sleep() {
        try {
            Thread.sleep(2 * 60 * 1000); // 2 minutes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
