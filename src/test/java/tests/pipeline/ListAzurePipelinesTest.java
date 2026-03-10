package tests.pipeline;

import api.pipeline.ListAzurePipelinesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class ListAzurePipelinesTest extends BaseTest {

    public void listAzurePipelines() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        if (projectId == null) {
            throw new RuntimeException("❌ ProjectId missing in ProjectStore.");
        }

        Report testData = JsonUtils.readJson(
                "testdata/pipeline/listAzurePipelines.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ listAzurePipelines.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> request =
                                (tc.getRequest() != null)
                                        ? new HashMap<>((Map<String, Object>) tc.getRequest())
                                        : new HashMap<>();

                        /* ✅ Inject dynamic values (same as your framework style) */
                        request.put("projectId", projectId.toString());
                        request.put("userId", userId);

                        tc.setRequest(request);

                        System.out.println("📦 List Azure Pipelines Payload → " + request);

                        Response response = ListAzurePipelinesApi.listPipelines(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ List Pipelines Failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        System.out.println("✅ Pipelines fetched successfully");

                        return response;
                    }
            );
        }
    }
}