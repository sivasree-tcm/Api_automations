package tests.prompt;

import api.prompt.UpdatePromptApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class UpdatePromptTest extends BaseTest {

    public void updatePromptApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/Prompt/updatePrompt.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ updatePrompt.json missing");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String,Object> request =
                    new HashMap<>((Map<String,Object>) tc.getRequest());

            // 🔹 Generate Prompt Type
            String promptType =
                    request.get("promptSource") +
                            "_TO_" +
                            request.get("promptDestination");

            // 🔹 Fetch stored promptId
            Integer promptId = PromptStore.getPromptId(promptType);

            if(promptId == null){
                throw new RuntimeException(
                        "❌ PromptId not found in PromptStore for type: "
                                + promptType
                );
            }

            // 🔹 Inject dynamic values
            request.put("promptId", promptId);
            request.put("userId", String.valueOf(TokenUtil.getUserId()));
            request.put("promptText", TestDataGenerator.generatePromptText());
            request.put(
                    "promptDescription",
                    TestDataGenerator.generateValidDescription()
            );

            tc.setRequest(request);

            // ✅ IMPORTANT FOR REPORT
            tc.setTcId("UPDATE_PROMPT_" + promptId);
            tc.setName("Update Prompt | Type=" + promptType);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> UpdatePromptApi.updatePrompt(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}