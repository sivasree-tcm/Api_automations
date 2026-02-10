package tests.project;

import api.generation.GenerateTSApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTSTest extends BaseTest {

    public void generateTSForBR() {

        // ✅ SAFE project resolution (NO exception)
        Integer projectId = ProjectStore.peekSelectedProjectId();

        if (projectId == null) {
            projectId = ProjectStore.getProjectId();

            if (projectId == null) {
                throw new RuntimeException(
                        "❌ No projectId available. Project creation step failed."
                );
            }

            ProjectStore.setSelectedProject(projectId);

            System.out.println(
                    "⚠️ selectedProjectId was missing. Set from projectId → "
                            + projectId
            );
        }

        // ✅ Fetch BRs
        List<Integer> brIds =
                BusinessRequirementStore.getBrIds(projectId);

        if (brIds == null || brIds.isEmpty()) {
            throw new RuntimeException(
                    "❌ No BRs found for project " + projectId
            );
        }

        // ✅ Select BRs (as per your logic)
        List<Integer> selectedBrs =
                brIds.size() >= 1 ? brIds.subList(0, 1) : brIds;

        GeneratedBRStore.store(selectedBrs);

        System.out.println(
                "🔹 BRs selected for TS generation → " + selectedBrs
        );

        BusinessRequirementStore.storeGeneratedBRs(
                projectId,
                selectedBrs
        );

        // ✅ Load test data
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/generation/generateTS.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        // ✅ Build request
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
