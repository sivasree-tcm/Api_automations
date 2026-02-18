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
        CreatePromptTestData testData = JsonUtils.readJson(
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
                        // Create a mutable copy of the request map
                        Map<String, Object> req = new HashMap<>(tc.getRequest());

                        // 1️⃣ Generate unique text to bypass "Duplicate prompt" error
                        String originalText = String.valueOf(tc.getRequest().get("promptText"));
                        String uniqueSuffix = "\n\n[Test Run ID: " + System.currentTimeMillis() + "]";
                        req.put("promptText", originalText + uniqueSuffix);

                        // 2️⃣ Inject Dynamic userId
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));

                        // 3️⃣ Call API
                        Response response = PromptApi.createPrompt(req, tc.getRole());

                        // ✅ Store promptId by TYPE if successful
                        if (response.getStatusCode() == 200) {
                            // Extract the ID (ensure 'insertedId' is the correct key from your response)
                            Integer promptId = response.jsonPath().get("insertedId");

                            if (promptId != null) {
                                String promptType = String.valueOf(tc.getRequest().get("promptType"));

                                // Store ID in PromptStore using the original Type (e.g., BR_TO_TS)
                                PromptStore.setPromptId(promptType, promptId);

                                System.out.println("✅ Successfully Created Prompt!");
                                System.out.println("Type: " + promptType);
                                System.out.println("ID: " + promptId);
                            }
                        } else {
                            // Log the error message if it's not a 200
                            System.out.println("❌ Create Prompt Failed: " + response.jsonPath().getString("error"));
                        }

                        return response;
                    }
            );
        }
    }
}