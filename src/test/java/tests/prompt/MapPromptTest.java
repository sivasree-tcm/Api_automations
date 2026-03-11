package tests.prompt;

import api.prompt.MapPromptApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.Map;
import java.util.HashMap;

public class MapPromptTest extends BaseTest {

    public void mapPromptApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/Prompt/mapprompt.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) return;

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request;

            if (tc.getRequest() != null) {
                request = new HashMap<>((Map<String, Object>) tc.getRequest());
            } else {
                request = new HashMap<>();
            }

            // 🔁 Handle Dynamic Project ID
            if ("DYNAMIC_PROJECT".equals(request.get("refprojectId"))) {
                request.put("refprojectId", ProjectStore.getProjectId());
            }

            // 🔑 PromptId logic
            String promptType = String.valueOf(request.get("promptType"));
            Integer promptId;

            switch (promptType) {

                case "BR_TO_TS":
                    promptId = 9;
                    break;

                case "TS_TO_TC":
                    promptId = 10;
                    break;

                default:
                    promptId = PromptStore.getPromptId(promptType);
            }

            if (promptId == null) {
                throw new IllegalStateException(
                        "❌ No promptId assigned for type: " + promptType
                );
            }

            // Inject promptId
            request.put("promptId", promptId);

            // 👤 Dynamic userId
            request.put("userId", TokenUtil.getUserId(tc.getRole()));

            // Store payload for report
            tc.setRequest(request);

            // 🚀 Execute API (FIXED: added authType)
            ApiTestExecutor.execute(
                    testData.getScenario() + " - " + promptType,
                    tc,
                    () -> MapPromptApi.mapPrompt(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}