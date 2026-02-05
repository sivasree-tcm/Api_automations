package tests.modelmapping;

import api.modelmapping.MapLlmToProjectApi;
import base.BaseTest;
import models.modelmapping.MapLlmReport;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.List;
import java.util.Map;

public class MapLlmToProjectTest extends BaseTest {

    @Test
    public void mapLlmToProjectTest() {

        MapLlmReport testData = JsonUtils.readJson(
                "testdata/model/mapLlmToProject.json",
                MapLlmReport.class
        );

        if (testData != null) {
            execute(testData, testData.getTestCases());
        }
    }

    private void execute(MapLlmReport testData,
                         List<MapLlmReport.TestCase> cases) {

        for (MapLlmReport.TestCase tc : cases) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            // userId
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            // projectId
            request.put("projectId", ProjectStore.getProjectId());

            // model selection (FIXED)
            String modelType = tc.getModelType();

            if ("chat".equalsIgnoreCase(modelType)) {
                request.put("model_id", ModelStore.getChatModelId());
            } else if ("multimodal".equalsIgnoreCase(modelType)) {
                request.put("model_id", ModelStore.getMultimodalModelId());
            } else {
                throw new IllegalArgumentException(
                        "Invalid modelType: " + modelType
                );
            }

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
