//package utils;
//
//import io.restassured.response.Response;
//import api.generation.GetTSByBRApi;
//
//import java.util.List;
//import java.util.Map;
//
//public class TestScenarioUtil {
//
//    public static Integer getLastTestScenarioId(
//            Integer projectId,
//            String role,
//            String authType
//    ) {
//
//        int pageSize = 10;
//
//        // 🔹 First call → get totalCount
//        Response firstResponse =
//                GetTSByBRApi.getTSByBrId(
//                        projectId,
//                        1,
//                        pageSize,
//                        role,
//                        authType
//                );
//
//        Integer totalCount =
//                firstResponse.jsonPath().getInt("totalCount");
//
//        if (totalCount == null || totalCount == 0) {
//            throw new RuntimeException("❌ No Test Scenarios found");
//        }
//
//        int lastPage =
//                (int) Math.ceil((double) totalCount / pageSize);
//
//        // 🔹 Second call → last page
//        Response lastPageResponse =
//                GetTestScenarioSummaryApi.getTestScenarios(
//                        projectId,
//                        lastPage,
//                        pageSize,
//                        role,
//                        authType
//                );
//
//        List<Map<String, Object>> results =
//                lastPageResponse.jsonPath().getList("results");
//
//        if (results == null || results.isEmpty()) {
//            throw new RuntimeException("❌ No TS found on last page");
//        }
//
//        Map<String, Object> lastTs =
//                results.get(results.size() - 1);
//
//        return ((Number) lastTs.get("testScenarioId")).intValue();
//    }
//}
