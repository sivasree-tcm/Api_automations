//package tests.generation;
//
//import api.generation.GetTCGenerationStatusApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import report.ConnectionReport;
//import tests.user.ApiTestExecutor;
//import utils.*;
//
//import java.util.*;
//
//public class WaitForTCCompletionTest extends BaseTest {
//
//    private static final int MAX_ATTEMPTS = 30;
//    private static final int WAIT_MS = 120_000; // 2 minutes
//
//    public void waitUntilAllCompleted() {
//
//        Integer projectId = ProjectStore.getSelectedProjectId();
//
//        // ✅ TS IDs generated in previous step
//        List<Integer> allTsIds = TestScenarioStore.getAllTS();
//
//        if (allTsIds == null || allTsIds.isEmpty()) {
//            throw new RuntimeException("❌ No TS found to wait for TC generation");
//        }
//
//        System.out.println("🧪 Waiting for TC generation for TS → " + allTsIds);
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/generation/getGenerationStatus.json",
//                        ConnectionReport.class
//                );
//
//        ConnectionReport.TestCase tc =
//                new ConnectionReport.TestCase(
//                        testData.getTestCases().get(0)
//                );
//
//        tc.setTcId("WAIT_TC_GEN_" + projectId);
//        tc.setName("Wait for TC Generation | Project " + projectId);
//
//        ApiTestExecutor.execute(
//                testData.getScenario(),
//                tc,
//                () -> {
//
//                    boolean completed = false;
//
//                    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
//
//                        Map<String, Object> request = new HashMap<>();
//                        request.put("projectId", projectId);
//                        request.put("source", "TS");          // ✅ IMPORTANT
//                        request.put("destination", "TC");
//                        request.put("userId", TokenUtil.getUserId());
//                        request.put("pending", allTsIds);    // ✅ ALL TS
//
//                        Response response =
//                                GetTCGenerationStatusApi.getStatus(
//                                        request,
//                                        tc.getRole(),
//                                        tc.getAuthType()
//                                );
//
//                        System.out.println(
//                                "Attempt " + attempt +
//                                        " | StatusCode=" + response.getStatusCode()
//                        );
//
//                        if (response.getStatusCode() != 200) {
//                            try {
//                                Thread.sleep(WAIT_MS);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            continue;
//                        }
//
//                        // ✅ RESPONSE IS ARRAY
//                        List<Map<String, Object>> statusList =
//                                response.jsonPath().getList("$");
//
//                        if (statusList == null || statusList.isEmpty()) {
//                            try {
//                                Thread.sleep(WAIT_MS);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            continue;
//                        }
//
//                        boolean allCompleted = true;
//
//                        for (Map<String, Object> item : statusList) {
//                            String status = String.valueOf(item.get("status"));
//                            Object tsId = item.get("tsId");
//
//                            System.out.println("TS " + tsId + " → " + status);
//
//                            if (!"Completed".equalsIgnoreCase(status)) {
//                                allCompleted = false;
//                                break;
//                            }
//                        }
//
//                        if (allCompleted) {
//                            System.out.println("🎉 TC generation COMPLETED for all TS");
//                            completed = true;
//                            break;
//                        }
//
//                        try {
//                            Thread.sleep(WAIT_MS);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    if (!completed) {
//                        throw new RuntimeException(
//                                "❌ TC generation did not complete within timeout"
//                        );
//                    }
//
//                    return null;
//                }
//        );
//    }
//}
