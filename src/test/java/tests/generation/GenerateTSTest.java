package tests.generation;

import api.generation.GenerateTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GenerateTSTest extends BaseTest {

    public void generateTSForBR() {

        // ✅ Get selected project
        Integer projectId = ProjectStore.getSelectedProjectId();

        // ✅ Get all BRs
        List<Integer> brIds =
                BusinessRequirementStore.getBrIds(projectId);

        if (brIds == null || brIds.isEmpty()) {
            throw new RuntimeException(
                    "❌ No BRs found for project " + projectId
            );
        }
        // ✅ TAKE ONLY FIRST 10 BRs
        List<Integer> selectedBrs =
                brIds.size() >= 1 ? brIds.subList(0, 1) : brIds;
        GeneratedBRStore.store(selectedBrs);


        System.out.println("🔹 BRs selected for TS generation → " + selectedBrs);

        // 🔥 FIX: STORE generated BRs for next step
        BusinessRequirementStore.storeGeneratedBRs(
                projectId,
                selectedBrs
        );

        Report testData =
                JsonUtils.readJson(
                        "testdata/generation/generateTS.json",
                        Report.class
                );

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        Map<String, Object> request = new HashMap<>();
        request.put("source", "BR");
        request.put("destination", "TS");
        request.put("sourceString", selectedBrs);
        request.put("projectId", projectId);
        request.put("userId", TokenUtil.getUserId());

        tc.setRequest(request);
        tc.setTcId("GEN_TS_" + projectId);
        tc.setName("Generate TS | BR " + selectedBrs);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {
                    Response response =
                            GenerateTSApi.generateTS(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    System.out.println(
                            "✅ TS generation queued for BRs → " + selectedBrs
                    );

                    return response;
                }
        );
    }
}