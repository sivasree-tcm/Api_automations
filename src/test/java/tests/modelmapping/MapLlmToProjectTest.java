package tests.modelmapping;

import api.modelmapping.MapLlmToProjectApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MapLlmToProjectTest extends BaseTest {

    public void mapLlmToProjectTest() {

        Report testData = JsonUtils.readJson(
                "testdata/model/mapLlmToProject.json",
                Report.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(Report testData,
                         List<Report.TestCase> cases) {

        for (Report.TestCase tc : cases) {

            Map<String, Object> request;

            if (tc.getRequest() != null) {
                request = new HashMap<>((Map<String, Object>) tc.getRequest());
            } else {
                request = new HashMap<>();
            }

            // ✅ userId injection
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            // ✅ projectId injection
            request.put("projectId", ProjectStore.getProjectId());

            // ✅ model selection logic
            String modelType = tc.getModelType();

            if ("chat".equalsIgnoreCase(modelType)) {
                request.put("model_id", ModelStore.getChatModelId());
            }
            else if ("multimodal".equalsIgnoreCase(modelType)) {
                request.put("model_id", ModelStore.getMultimodalModelId());
            }
            else {
                throw new IllegalArgumentException(
                        "Invalid modelType: " + modelType
                );
            }

            // ✅ store payload in report
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> MapLlmToProjectApi.mapModelToProject(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}