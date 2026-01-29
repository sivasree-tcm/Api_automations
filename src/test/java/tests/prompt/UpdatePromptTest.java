package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.PromptStore;

import java.util.List;

public class UpdatePromptTest extends BaseTest {
    @Test(dependsOnGroups = "prompt-create")

    public void updatePromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/Prompt/UpdatePrompt.json",
                        CreatePromptTestData.class
                );

        if (testData == null || testData.getTestCases() == null) return;

        for (CreatePromptTestData.TestCase tc : testData.getTestCases()) {

            Integer promptId = PromptStore.getPromptId();

            if (promptId == null) {
                throw new RuntimeException(
                        "❌ promptId not found. Run GetPrompt before UpdatePrompt."
                );
            }

            // ✅ Inject dynamic promptId
            tc.getRequest().put("promptId", promptId);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> PromptApi.updatePrompt(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}
