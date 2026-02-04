package tests.generation;

import api.generation.GetGenerationStatusApi;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetGenerationStatusTest {

    public void waitUntilAllCompleted() {

        Integer projectId = ProjectStore.getSelectedProjectId();

        // ✅ BRs generated in step9
        List<Integer> allBrIds =
                BusinessRequirementStore.getGeneratedBRs(projectId);

        if (allBrIds.isEmpty()) {
            throw new RuntimeException("❌ No BRs found to check generation status");
        }

        System.out.println("🧪 BRs used for generation status → " + allBrIds);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/generation/getGenerationStatus.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_GEN_STATUS_" + projectId);
        tc.setName("Get Generation Status | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    List<Integer> pending = new ArrayList<>(allBrIds);
                    List<Integer> completed = new ArrayList<>();

                    // 🔁 Poll until all BRs are completed
                    while (!pending.isEmpty()) {

                        List<Integer> stillPending = new ArrayList<>();

                        for (Integer brId : pending) {

                            Map<String, Object> request = new HashMap<>();
                            request.put("projectId", projectId);
                            request.put("source", "BR");
                            request.put("userId", TokenUtil.getUserId());
                            request.put("pending", List.of(brId)); // 🔥 ONE BY ONE

                            Response response =
                                    GetGenerationStatusApi.getStatus(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            // ✅ Handle BOTH List and Map responses
                            Object responseBody = response.jsonPath().get("$");

                            if (responseBody == null) {
                                stillPending.add(brId);
                                continue;
                            }

                            boolean isCompleted = false;

                            if (responseBody instanceof List) {
                                // Response is an array
                                List<Map<String, Object>> statusList =
                                        response.jsonPath().getList("$");

                                if (statusList.isEmpty()) {
                                    stillPending.add(brId);
                                    continue;
                                }

                                // Find matching brId in response
                                for (Map<String, Object> item : statusList) {
                                    Integer responseBrId = (Integer) item.get("brId");
                                    if (brId.equals(responseBrId)) {
                                        String status = (String) item.get("status");
                                        isCompleted = "Completed".equalsIgnoreCase(status);
                                        break;
                                    }
                                }

                            } else if (responseBody instanceof Map) {
                                // Response is a single object (usually when completed)
                                Map<String, Object> statusMap =
                                        response.jsonPath().getMap("$");

                                Integer responseBrId = (Integer) statusMap.get("brId");
                                if (brId.equals(responseBrId)) {
                                    String status = (String) statusMap.get("status");
                                    isCompleted = "Completed".equalsIgnoreCase(status);
                                }
                            }

                            if (isCompleted) {
                                if (!completed.contains(brId)) {
                                    completed.add(brId);
                                }
                            } else {
                                stillPending.add(brId);
                            }
                        }

                        System.out.println("✅ Completed BRs → " + completed);
                        System.out.println("⏳ Pending BRs → " + stillPending);

                        pending = stillPending;

                        if (!pending.isEmpty()) {
                            try {
                                Thread.sleep(2 * 60 * 1000); // ⏱ 2 minutes
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }

                    // ✅ Persist completed BRs for next flow
                    BusinessRequirementStore.storeCompletedBRs(
                            projectId,
                            completed
                    );

                    System.out.println(
                            "🎉 ALL BRs completed for project " +
                                    projectId + " → " + completed
                    );

                    // 🔥 FINAL SINGLE REPORT CALL
                    Map<String, Object> finalRequest = new HashMap<>();
                    finalRequest.put("projectId", projectId);
                    finalRequest.put("source", "BR");
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

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(testData.getTestCases().get(0));

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

                            Response response =
                                    GetGenerationStatusApi.getStatus(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            Object body = response.jsonPath().get("$");
                            boolean isCompleted = false;

                            if (body instanceof List) {
                                List<Map<String, Object>> list = response.jsonPath().getList("$");
                                if (!list.isEmpty()) {
                                    String status = String.valueOf(list.get(0).get("status"));
                                    isCompleted = "Completed".equalsIgnoreCase(status);
                                }
                            } else if (body instanceof Map) {
                                String status = String.valueOf(((Map<?, ?>) body).get("status"));
                                isCompleted = "Completed".equalsIgnoreCase(status);
                            }

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
                            try {
                                Thread.sleep(2 * 60 * 1000); // 2 minutes
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    System.out.println(
                            "🎉 ALL TS completed for project " +
                                    projectId + " → " + completed
                    );

                    // ✅ FINAL RESPONSE FOR REPORT - MUST BE INSIDE execute()
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
                }  // 🔥 CLOSING BRACE OF execute() - Everything must be BEFORE this
        );  // 🔥 CLOSING PARENTHESIS OF execute()
    }

}