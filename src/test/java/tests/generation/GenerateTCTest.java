package tests.generation;

import api.generation.GenerateTCApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.*;

import java.util.*;

public class GenerateTCTest extends BaseTest {


    public void generateTCForSelectedTS() {

        if (!TestScenarioStore.hasTS()) {
            throw new RuntimeException("❌ No TS available for TC generation");
        }

        // ✅ Get all available BR IDs with TS
// ✅ OPTION 1: Get BRs that have TS generated
        List<Integer> allBrIds = new ArrayList<>(TestScenarioStore.getAllBrIds());
        if (allBrIds.isEmpty()) {
            throw new RuntimeException("❌ No BRs with TS found");
        }

        // ✅ MANUAL SELECTION - Take only first BR (or customize as needed)
        List<Integer> selectedBrIds = allBrIds.subList(0, 1);

        System.out.println("🔹 Selected BRs for TC generation → " + selectedBrIds);

        for (Integer brId : selectedBrIds) {

            List<Integer> tsIds = TestScenarioStore.getTsByBr(brId);

            if (tsIds.isEmpty()) continue;

            // ✅ MANUAL TS SELECTION - Take only first 5 TS (customize as needed)
            List<Integer> selectedTsIds =
                    tsIds.size() >=1 ? tsIds.subList(0, 1) : tsIds;
            GeneratedTSStore.store(selectedTsIds);

            System.out.println("🔹 Selected TS for TC generation → " + selectedTsIds);

            Map<String, Object> request = new HashMap<>();
            request.put("source", "TS");
            request.put("destination", "TC");
            request.put("sourceString", selectedTsIds);
            request.put("userId", TokenUtil.getUserId());
            request.put("projectId", ProjectStore.getSelectedProjectId());
            request.put("refBrId", String.valueOf(brId)); // Ensure this is a String if your UI does that

            // 🔥 FIX: Change "undefined" to a valid value like "REGRESSION" or "SMOKE"
            request.put("testType", "REGRESSION");

            request.put("environmentName", "Ticking");
            request.put("generationConfirmation", "NOW");
            request.put("testDataGeneration", "NOW");

            Response response = GenerateTCApi.generateTC(request, "SUPER_ADMIN", "VALID");



            if (response.getStatusCode() != 200) {
                throw new RuntimeException("❌ TC generation API failed");
            }

            String batchId = response.jsonPath().getString("batchId");

            if (batchId == null || batchId.isEmpty()) {
                throw new RuntimeException("❌ TC batchId not returned");
            }

            BatchStore.addBatchId(batchId);

            System.out.println("✅ TC generation QUEUED for BR " + brId);
            System.out.println("📦 BatchId: " + batchId);
            System.out.println("📋 TS Count: " + selectedTsIds.size());
        }
    }
}
//package tests.generation;
//
//import api.generation.GenerateTCApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import org.testng.annotations.Test;
//import utils.*;
//
//import java.util.*;
//
//public class GenerateTCTest extends BaseTest {
//
//    @Test
//    public void generateTCForSelectedTS() {
//        // We'll keep the loop logic but use the exact hardcoded values you provided
//        Map<String, Object> request = new HashMap<>();
//
//        // 🛠️ EXACT HARDCODED VALUES FROM YOUR JSON
//        request.put("source", "TS");
//        request.put("destination", "TC");
//
//        // Using the specific ID [5936] from your snippet
//        List<Integer> sourceString = Collections.singletonList(5944);
//        request.put("sourceString", sourceString);
//
//        request.put("userId", "28");
//        request.put("projectId", 112);
//
//        // Changed "undefined" to "REGRESSION" to ensure the LLM processes it
//        request.put("testType", "REGRESSION");
//
//        request.put("refBrId", "577");
//        request.put("environmentName", "Ticking");
//        request.put("generationConfirmation", "NOW");
//        request.put("testDataGeneration", "NOW");
//
//        System.out.println("🚀 Sending Request Payload: " + request);
//
//        // 🚀 Execute API call
//        Response response = GenerateTCApi.generateTC(request, "SUPER_ADMIN", "VALID");
//
//        // Logging the response for debugging
//        System.out.println("📊 Response Status Code: " + response.getStatusCode());
//        System.out.println("📝 Response Body: " + response.getBody().asString());
//
//        if (response.getStatusCode() == 200) {
//            String batchId = response.jsonPath().getString("batchId");
//            if (batchId != null) {
//                BatchStore.addBatchId(batchId);
//                System.out.println("✅ TC generation QUEUED. BatchId: " + batchId);
//            }
//        } else {
//            throw new RuntimeException("❌ TC generation failed with status: " + response.getStatusCode());
//        }
//    }
