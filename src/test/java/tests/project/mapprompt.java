package tests.project;

import api.prompt.PromptApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.prompt.CreatePromptTestData;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.Map;

public class mapprompt extends BaseTest {

    public void mapPromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/project/mapprompt.json",
                        CreatePromptTestData.class
                );

        if (testData == null || testData.getTestCases() == null) return;

        for (CreatePromptTestData.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = tc.getRequest();

            // 🔁 Dynamic projectId
            if ("DYNAMIC_PROJECT".equals(request.get("refprojectId"))) {
                request.put(
                        "refprojectId",
                        ProjectStore.getProjectId()
                );
            }

            // 🔑 Get prompt by TYPE
            String promptType =
                    String.valueOf(request.get("promptType"));

            Integer promptId =
                    PromptStore.getPromptId(promptType);

            if (promptId == null) {
                throw new IllegalStateException(
                        "❌ No promptId found for type: " + promptType
                );
            }

            request.put("promptId", promptId);

            // 🔁 Dynamic userId
            request.put(
                    "userId",
                    TokenUtil.getUserId(tc.getRole())
            );

            ApiTestExecutor.execute(
                    testData.getScenario() + " - " + promptType,
                    tc,
                    () -> PromptApi.mapPrompt(
                            request,
                            tc.getRole()
                    )
            );
        }
    }
}
