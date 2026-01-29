package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.PromptStore;

import java.util.List;
import java.util.Map;

public class CreatePromptTest extends BaseTest {

    @Test(groups = "prompt-create")
    public void createPromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/Prompt/createprompt.json",
                        CreatePromptTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            CreatePromptTestData testData,
            List<CreatePromptTestData.TestCase> cases
    ) {

        if (cases == null || cases.isEmpty()) return;

        for (CreatePromptTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                PromptApi.createPrompt(
                                        tc.getRequest(),
                                        tc.getRole()
                                );

                        // ✅ Store dynamic promptId
                        if (response.getStatusCode() == 200) {
                            Integer promptId =
                                    response.jsonPath().getInt("insertedId");

                            if (promptId != null) {
                                PromptStore.setPromptId(promptId);
                                System.out.println(
                                        "✅ Prompt created with ID: " + promptId
                                );
                            }
                        }

                        return response;
                    }
            );
        }
    }
}
