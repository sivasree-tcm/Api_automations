package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.PromptStore;

import java.util.List;

public class GetPromptTest extends BaseTest {

    @Test(dependsOnGroups = "prompt-create")
    public void getPromptApiTest() {

        GetPromptTestData testData =
                JsonUtils.readJson(
                        "testdata/Prompt/getPrompt.json",
                        GetPromptTestData.class
                );

        if (testData == null || testData.getTestCases() == null) {
            return;
        }

        execute(testData, testData.getTestCases());
    }

    private void execute(
            GetPromptTestData testData,
            List<GetPromptTestData.TestCase> cases
    ) {

        for (GetPromptTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        var response = PromptApi.getAllPrompts(
                                tc.getRequest(),
                                tc.getRole()
                        );

                        // ✅ Extract & store promptId safely
                        if (response.getStatusCode() == 200) {

                            List<Integer> promptIds =
                                    response.jsonPath().getList("prompts.promptId");

                            if (promptIds != null && !promptIds.isEmpty()) {

                                Integer firstPromptId = promptIds.get(0);

                                // Store ONLY if not already stored
                                if (!PromptStore.hasPromptId()) {
                                    PromptStore.setPromptId(firstPromptId);
                                    System.out.println("📌 Stored Prompt ID: " + firstPromptId);
                                }

                            } else {
                                System.out.println("ℹ️ No prompts found for this user");
                            }
                        }

                        return response;
                    }
            );
        }
    }
}
