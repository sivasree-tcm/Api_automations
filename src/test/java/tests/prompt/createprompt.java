package tests.prompt;

import api.prompt.PromptApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createprompt extends BaseTest {

    public void createPromptApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/Prompt/createprompt.json",
                Report.class
        );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            Report testData,
            List<Report.TestCase> cases
    ) {

        if (cases == null || cases.isEmpty()) return;

        for (Report.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> req;

                        if (tc.getRequest() != null) {
                            req = new HashMap<>((Map<String, Object>) tc.getRequest());
                        } else {
                            req = new HashMap<>();
                        }

                        // 1️⃣ Generate unique prompt text
                        String originalText = String.valueOf(req.get("promptText"));
                        String uniqueSuffix = "\n\n[Test Run ID: " + System.currentTimeMillis() + "]";

                        req.put("promptText", originalText + uniqueSuffix);

                        // 2️⃣ Inject dynamic userId
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));

                        // 3️⃣ Store payload in report
                        tc.setRequest(req);

                        // 4️⃣ Call API
                        Response response = PromptApi.createPrompt(
                                req,
                                tc.getRole()
                        );

                        // 5️⃣ Store promptId if successful
                        if (response.getStatusCode() == 200) {

                            Integer promptId = response.jsonPath().get("insertedId");

                            if (promptId != null) {

                                String promptType = String.valueOf(req.get("promptType"));

                                PromptStore.setPromptId(promptType, promptId);

                                System.out.println("✅ Successfully Created Prompt!");
                                System.out.println("Type: " + promptType);
                                System.out.println("ID: " + promptId);
                            }

                        } else {

                            System.out.println(
                                    "❌ Create Prompt Failed: "
                                            + response.jsonPath().getString("error")
                            );
                        }

                        return response;
                    }
            );
        }
    }
}