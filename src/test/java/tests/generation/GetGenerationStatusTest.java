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

        List<Integer> allBrIds =
                BusinessRequirementStore.getGeneratedBRs(projectId);

        if (allBrIds == null || allBrIds.isEmpty()) {
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

        tc.setTcId("WAIT_BR_GEN_STATUS_" + projectId);
        tc.setName("Wait for BR Generation Completion | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    List<Integer> pending = new ArrayList<>(allBrIds);
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

                            Object body = response.jsonPath().get("$");

                            if (body == null) {
                                stillPending.add(brId);
                                continue;
                            }

                            String status = null;
                            boolean isCompleted = false;

                            if (body instanceof List) {

                                List<Map<String, Object>> list =
                                        response.jsonPath().getList("$");

                                if (list == null || list.isEmpty()) {
                                    stillPending.add(brId);
                                    continue;
                                }

                                for (Map<String, Object> item : list) {

                                    Integer responseBrId =
                                            (Integer) item.get("brId");

                                    if (brId.equals(responseBrId)) {

                                        status = String.valueOf(item.get("status"));

                                        // ❌ HARD STOP ON FAILURE
                                        if ("Failed".equalsIgnoreCase(status)) {
                                            System.out.println("❌ Generation FAILED for BR → " + brId);
                                            return response;
                                        }

                                        isCompleted =
                                                "Completed".equalsIgnoreCase(status);

                                        break;
                                    }
                                }

                            } else if (body instanceof Map) {

                                Map<String, Object> map =
                                        response.jsonPath().getMap("$");

                                Integer responseBrId =
                                        (Integer) map.get("brId");

                                status = String.valueOf(map.get("status"));

                                if ("Failed".equalsIgnoreCase(status)) {
                                    System.out.println("❌ Generation FAILED for BR → " + brId);
                                    return response;
                                }

                                isCompleted =
                                        brId.equals(responseBrId)
                                                && "Completed".equalsIgnoreCase(status);
                            }

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
                            try {
                                Thread.sleep(1 * 60 * 1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException(e);
                            }
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

}
