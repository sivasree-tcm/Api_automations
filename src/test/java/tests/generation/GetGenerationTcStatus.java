package tests.generation;

import api.generation.GetGenerationStatusApi;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.GeneratedTSStore;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.*;

public class GetGenerationTCStatus {

    public void waitUntilAllCompletedForTC() {
        Integer projectId = ProjectStore.getSelectedProjectId();
        List<Integer> tsIds = GeneratedTSStore.getAll();

        if (tsIds == null || tsIds.isEmpty()) {
            throw new RuntimeException("❌ No TS available for TC generation status check");
        }

        System.out.println("🧪 TS used for TC generation status → " + tsIds);

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/generation/getGenerationStatus.json",
                ConnectionReport.class
        );

        ConnectionReport.TestCase tc = new ConnectionReport.TestCase(testData.getTestCases().get(0));
        tc.setTcId("WAIT_TC_GEN_STATUS_" + projectId);
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

                            Response response = GetGenerationStatusApi.getStatus(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                            // --- FLEXIBLE HANDLING START ---
                            Object rawResponse = response.jsonPath().get("$");
                            List<Map<String, Object>> items = new ArrayList<>();

                            if (rawResponse instanceof List) {
                                // API returned an Array []
                                items = (List<Map<String, Object>>) rawResponse;
                            } else if (rawResponse instanceof Map) {
                                // API returned a single Object {}
                                items.add((Map<String, Object>) rawResponse);
                            }
                            // --- FLEXIBLE HANDLING END ---

                            if (items.isEmpty()) {
                                stillPending.add(tsId);
                                continue;
                            }

                            boolean isCompleted = false;
                            for (Map<String, Object> item : items) {
                                Object resRefIdObj = item.get("refId");
                                Object statusObj = item.get("status");

                                if (resRefIdObj != null && statusObj != null) {
                                    Integer responseTsId = Integer.valueOf(String.valueOf(resRefIdObj));
                                    String status = String.valueOf(statusObj);

                                    if (responseTsId.equals(tsId) && "Completed".equalsIgnoreCase(status)) {
                                        isCompleted = true;
                                        break;
                                    }
                                }
                            }

                            if (isCompleted) {
                                if (!completed.contains(tsId)) completed.add(tsId);
                            } else {
                                stillPending.add(tsId);
                            }
                        }

                        System.out.println("✅ Completed TS → " + completed);
                        System.out.println("⏳ Pending TS → " + stillPending);

                        pending = stillPending;

                        if (!pending.isEmpty()) {
                            try {
                                Thread.sleep(2 * 60 * 1000); // 2 minutes
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    System.out.println("🎉 ALL TS completed for project " + projectId + " → " + completed);

                    Map<String, Object> finalRequest = new HashMap<>();
                    finalRequest.put("projectId", projectId);
                    finalRequest.put("source", "TS");
                    finalRequest.put("userId", TokenUtil.getUserId());
                    finalRequest.put("pending", completed);

                    return GetGenerationStatusApi.getStatus(
                            finalRequest,
                            tc.getRole(),
                            tc.getAuthType()
                    );
                }
        );
    }
}