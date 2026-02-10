package tests.project;

import api.prompt.PromptApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.prompt.CreatePromptTestData;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.PromptStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createprompt extends BaseTest {

    @Test
    public void createPromptApiTest() {

        CreatePromptTestData testData =
                JsonUtils.readJson(
                        "testdata/project/createprompt.json",
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

                        Map<String, Object> req =
                                new HashMap<>(tc.getRequest());

                        // 🔥 Dynamic userId
                        req.put(
                                "userId",
                                TokenUtil.getUserId(tc.getRole())
                        );

                        Response response =
                                PromptApi.createPrompt(
                                        req,
                                        tc.getRole()
                                );

                        // ✅ Store promptId by TYPE
                        if (response.getStatusCode() == 200) {

                            Integer promptId =
                                    response.jsonPath()
                                            .getInt("insertedId");

                            if (promptId != null) {

                                // 🔑 Decide type from test data
                                String promptType =
                                        String.valueOf(
                                                tc.getRequest()
                                                        .get("promptType")
                                        );

                                PromptStore.setPromptId(
                                        promptType,
                                        promptId
                                );

                                System.out.println(
                                        "✅ Prompt created [" + promptType +
                                                "] with ID: " + promptId
                                );
                            }
                        }

                        return response;
                    }
            );
        }
    }
}
