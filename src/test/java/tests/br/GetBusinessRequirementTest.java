package tests.br;

import api.br.GetBRByProjectApi;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetBusinessRequirementTest {

    public void fetchBRsForProject() {

        // Safe project resolution
        Integer projectId = ProjectStore.peekSelectedProjectId();

        if (projectId == null) {

            projectId = ProjectStore.getProjectId();

            if (projectId == null) {
                throw new RuntimeException(
                        "❌ Project ID not available. Project creation failed."
                );
            }

            ProjectStore.setSelectedProject(projectId);

            System.out.println(
                    "⚠️ selectedProjectId was missing. Auto-set → " + projectId
            );
        }

        final Integer finalProjectId = projectId;

        Report testData =
                JsonUtils.readJson(
                        "testdata/br/getBRByProject.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getBRByProject.json missing");
        }

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        tc.setTcId("GET_BR_" + finalProjectId);
        tc.setName("Get BR | Project " + finalProjectId);

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

                            Map<String, Object> request =
                                    (tc.getRequest() != null)
                                            ? new HashMap<>((Map<String, Object>) tc.getRequest())
                                            : new HashMap<>();

                            request.put("projectId", finalProjectId);
                            request.put("page", page);
                            request.put("pageSize", pageSize);
                            request.put("userId", TokenUtil.getUserId(tc.getRole()));

                            tc.setRequest(request);

                            System.out.println("REQUEST → " + request);

                            Response response =
                                    GetBRByProjectApi.getBRs(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            lastResponse = response;

                            if (response.getStatusCode() != 200) {
                                throw new RuntimeException(
                                        "❌ Get BR API failed → " + response.asString()
                                );
                            }

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
                                "⏳ BRs not ready yet for project " + finalProjectId +
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
                                "❌ No BRs found for project " + finalProjectId +
                                        " after retries"
                        );
                    }

                    BusinessRequirementStore.store(finalProjectId, brIds);

                    System.out.println(
                            "✅ BR IDs stored → " + brIds
                    );

                    return lastResponse;
                }
        );
    }
}