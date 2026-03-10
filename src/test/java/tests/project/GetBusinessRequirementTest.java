package tests.project;

import api.br.GetBRByProjectApi;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetBusinessRequirementTest {

    public void fetchBRsForProject() {

        // ✅ SAFE project resolution (NO crash)
        Integer projectId = ProjectStore.peekSelectedProjectId();

        if (projectId == null) {
            projectId = ProjectStore.getProjectId();

            if (projectId == null) {
                throw new RuntimeException(
                        "❌ Project ID not available. Project creation failed."
                );
            }

            // 🔥 IMPORTANT: mark project as selected
            ProjectStore.setSelectedProject(projectId);

            System.out.println(
                    "⚠️ selectedProjectId was missing. Auto-set → " + projectId
            );
        }

        Report testData =
                JsonUtils.readJson(
                        "testdata/br/getBRByProject.json",
                        Report.class
                );

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_BR_" + projectId);
        tc.setName("Get BR | Project " + projectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    int pageSize = 10;
                    int maxRetries = 5;
                    int retry = 0;

                    List<Integer> brIds = new ArrayList<>();
                    Response lastResponse = null;

                    while (retry < maxRetries) {

                        brIds.clear();
                        int page = 1;

                        while (true) {

                            Map<String, Object> request = new HashMap<>();
                            request.put("projectId", ProjectStore.getProjectId());
                            request.put("page", page);
                            request.put("pageSize", pageSize);
                            request.put("userId", TokenUtil.getUserId());

                            Response response =
                                    GetBRByProjectApi.getBRs(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            lastResponse = response;

                            List<Map<String, Object>> data =
                                    response.jsonPath().getList("data");

                            if (data == null || data.isEmpty()) break;

                            for (Map<String, Object> br : data) {
                                Object id = br.get("brId");
                                if (id instanceof Number) {
                                    brIds.add(((Number) id).intValue());
                                }
                            }

                            page++;
                        }

                        if (!brIds.isEmpty()) break;

                        retry++;
                        System.out.println(
                                "⏳ BRs not ready yet for project " + ProjectStore.getProjectId() +
                                        " → retry " + retry + "/" + maxRetries
                        );

                        try {
                            Thread.sleep(10_000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    }

                    if (brIds.isEmpty()) {
                        throw new RuntimeException(
                                "❌ No BRs found for project " + ProjectStore.getProjectId() +
                                        " after retries"
                        );
                    }

                    // ✅ Store BRs for TS generation
                    BusinessRequirementStore.store(ProjectStore.getProjectId(), brIds);

                    System.out.println(
                            "✅ BR IDs stored → " + brIds
                    );

                    return lastResponse;
                }
        );
    }
}
