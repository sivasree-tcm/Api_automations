package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.PromptStore;

import java.util.Map;

public class MapPromptTest extends BaseTest {

    @Test
    public void mapPromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/prompt/MapPrompt.json",
                        CreatePromptTestData.class
                );

        if (testData == null || testData.getTestCases() == null) return;

        for (CreatePromptTestData.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = tc.getRequest();

            // 🔁 Replace dynamic projectId ONLY if present
            if (request.containsKey("refprojectId")
                    && "DYNAMIC_PROJECT".equals(request.get("refprojectId"))) {

                request.put(
                        "refprojectId",
                        ProjectStore.getAnyProjectId()
                );
            }

            // 🔁 Replace dynamic promptId ONLY if present
            if (request.containsKey("promptId")
                    && "DYNAMIC_PROMPT".equals(request.get("promptId"))) {

                request.put(
                        "promptId",
                        PromptStore.getPromptId()
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> PromptApi.mapPrompt(
                            request,
                            tc.getRole()
                    )
            );
        }
    }
}
