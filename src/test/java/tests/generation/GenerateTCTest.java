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
        List<Integer> selectedBrIds = allBrIds.subList(0, 2);

        System.out.println("🔹 Selected BRs for TC generation → " + selectedBrIds);

        for (Integer brId : selectedBrIds) {

            List<Integer> tsIds = TestScenarioStore.getTsByBr(brId);

            if (tsIds.isEmpty()) continue;

            // ✅ MANUAL TS SELECTION - Take only first 5 TS (customize as needed)
            List<Integer> selectedTsIds =
                    tsIds.size() >=2 ? tsIds.subList(0, 2) : tsIds;
            GeneratedTSStore.store(selectedTsIds);

            System.out.println("🔹 Selected TS for TC generation → " + selectedTsIds);

            Map<String, Object> request = new HashMap<>();
            request.put("source", "TS");
            request.put("destination", "TC");
            request.put("sourceString", selectedTsIds); // ✅ Use selected TS only
            request.put("userId", TokenUtil.getUserId());
            request.put("projectId", ProjectStore.getSelectedProjectId());
            request.put("refBrId", brId);
            request.put("generationConfirmation", "NOW");

            Response response =
                    GenerateTCApi.generateTC(
                            request,
                            "SUPER_ADMIN",
                            "VALID"
                    );

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