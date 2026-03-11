package tests.prompt;

import api.prompt.GetAllPromptApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class GetAllPromptTest extends BaseTest {

    public void getPromptApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/Prompt/getPrompt.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new IllegalStateException("❌ Prompt test data is missing");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        /* -------- BUILD REQUEST DYNAMICALLY -------- */

                        Map<String, Object> req = new HashMap<>();

                        req.put(
                                "userId",
                                TokenUtil.getUserId(tc.getRole())
                        );

                        System.out.println("📦 Final GetPrompt Payload → " + req);

                        /* -------- CALL API -------- */

                        Response response = GetAllPromptApi.getAllPrompts(
                                req,
                                tc.getRole()
                        );

                        /* -------- STORE PROMPT ID -------- */


                        return response;
                    }
            );
        }
    }
}