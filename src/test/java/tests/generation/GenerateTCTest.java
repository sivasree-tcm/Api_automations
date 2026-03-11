package tests.generation;

import api.generation.GenerateTCApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GenerateTCTest extends BaseTest {

    public void generateTCForSelectedTS() {

        if (!TestScenarioStore.hasTS()) {
            throw new RuntimeException("❌ No TS available for TC generation");
        }

        Integer projectId = ProjectStore.getSelectedProjectId();

        List<Integer> allBrIds = new ArrayList<>(TestScenarioStore.getAllBrIds());

        if (allBrIds.isEmpty()) {
            throw new RuntimeException("❌ No BRs with TS found");
        }

        List<Integer> selectedBrIds = allBrIds.subList(0, 1);

        System.out.println("🔹 Selected BRs for TC generation → " + selectedBrIds);

        Report testData =
                JsonUtils.readJson(
                        "testdata/generation/generateTC.json",
                        Report.class
                );

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        for (Integer brId : selectedBrIds) {

            List<Integer> tsIds = TestScenarioStore.getTsByBr(brId);

            if (tsIds == null || tsIds.isEmpty()) {

                System.out.println("❌ No TS found for BR → " + brId);
                continue;
            }

            List<Integer> selectedTsIds =
                    tsIds.size() >= 1 ? tsIds.subList(0, 1) : tsIds;

            GeneratedTSStore.store(selectedTsIds);

            System.out.println("🔹 Selected TS for TC generation → " + selectedTsIds);

            Map<String, Object> request = new HashMap<>();
            request.put("source", "TS");
            request.put("destination", "TC");
            request.put("sourceString", selectedTsIds);
            request.put("userId", TokenUtil.getUserId());
            request.put("projectId", projectId);
            request.put("refBrId", String.valueOf(brId));

            request.put("testType", "REGRESSION");

            // ✅ UPDATED: Fetching dynamic environment name from ProjectStore
            String dynamicEnv = ProjectStore.getEnvironmentName();
            request.put("environmentName", (dynamicEnv != null) ? dynamicEnv : "Ticking");

            request.put("generationConfirmation", "NOW");
            request.put("testDataGeneration", "NOW");

            tc.setRequest(request);
            tc.setTcId("GEN_TC_" + projectId);
            tc.setName("Generate TC | TS " + selectedTsIds);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GenerateTCApi.generateTC(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            System.out.println("❌ Generate TC API returned NULL");
                            return null;
                        }

                        System.out.println("📡 Status Code → " + response.getStatusCode());

                        String batchId =
                                response.jsonPath().getString("batchId");

                        if (response.getStatusCode() == 200 &&
                                batchId != null &&
                                !batchId.isEmpty()) {

                            BatchStore.addBatchId(batchId);

                            System.out.println("✅ TC generation QUEUED");
                            System.out.println("📦 BatchId → " + batchId);
                            System.out.println("📋 TS Count → " + selectedTsIds.size());

                        } else {

                            System.out.println("❌ TC generation FAILED");
                            System.out.println("Response → " + response.asPrettyString());
                        }

                        return response;
                    }
            );
        }
    }
}