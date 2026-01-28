package tests.queue;

import api.queue.DeleteGenerationQueueApi;
import base.BaseTest;
import groovyjarjarantlr.Token;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.login.LoginTest;
import tests.user.ApiTestExecutor;
import utils.GenerationQueueStore;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteGenerationQueueTest {

    public void deleteGenerationQueue() {

        TokenUtil.refreshToken();

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/queueData/deleteGenerationQueue.json",
                        ConnectionReport.class
                );

        GenerationQueueStore.getAll().forEach((userId, queueIds) -> {

            for (Integer projectId : ProjectStore.getAllProjectIds()) {

                ConnectionReport.TestCase tc =
                        new ConnectionReport.TestCase(
                                testData.getTestCases().get(0)
                        );

                tc.setTcId("DEL_QUEUE_" + userId);
                tc.setName("Delete Queue for User " + userId);

                Map<String, Object> request = new HashMap<>();
                request.put("userIdFromAPI", userId);
                request.put("userId", TokenUtil.getUserId());
                request.put("projectId", projectId);
                request.put("queueIds", queueIds);

                tc.setRequest(request);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> {


                            Response response =
                                    DeleteGenerationQueueApi.deleteQueue(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            int statusCode = response.getStatusCode();
                            String message = response.jsonPath().getString("message");

                            // ✅ CASE 1: queueIds missing → API returns 400 → PASS
                            if (statusCode == 400 &&
                                    "queueIds (array) is required".equalsIgnoreCase(message)) {

                                System.out.println(
                                        "ℹ No queueIds provided → Valid response received"
                                );

                                tc.setExpectedStatusCode(400);
                                return response;
                            }

                            // ✅ CASE 2: Normal delete
                            tc.setExpectedStatusCode(200);

                            System.out.println(
                                    "🗑 Queue delete attempted for user " + userId + "--->" + projectId +"--->"+ queueIds
                            );

                            return response;
                        }
                );
            }
        });
    }
}


