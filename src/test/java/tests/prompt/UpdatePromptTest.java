package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.PromptStore;

import java.util.Map;

public class UpdatePromptTest extends BaseTest {

    @Test
    public void updatePromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/Prompt/UpdatePrompt.json",
                        CreatePromptTestData.class
                );

        if (testData == null || testData.getTestCases() == null) return;

        for (CreatePromptTestData.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = tc.getRequest();

            // 🔐 Only replace promptId if it is explicitly present in JSON
            if (request.containsKey("promptId")) {

                Object value = request.get("promptId");

                if ("DYNAMIC".equals(value)) {
                    Integer promptId = PromptStore.getPromptId();
                    if (promptId == null) {
                        throw new RuntimeException(
                                "❌ promptId not found. Run Create/Get Prompt before UpdatePrompt."
                        );
                    }
                    request.put("promptId", promptId);
                }
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> PromptApi.updatePrompt(
                            request,
                            tc.getRole()
                    )
            );
        }
    }
}
