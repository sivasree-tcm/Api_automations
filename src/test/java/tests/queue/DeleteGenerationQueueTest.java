package tests.queue;

import api.queue.DeleteGenerationQueueApi;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.GenerationQueueStore;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class DeleteGenerationQueueTest {

    public void deleteGenerationQueue() {
        TokenUtil.refreshToken();
        Report testData =
                JsonUtils.readJson(
                        "testdata/queueData/deleteGenerationQueue.json",
                        Report.class
                );

        GenerationQueueStore.getAll().forEach((projectId, userMap) -> {

            userMap.forEach((userId, queueIds) -> {

                // ✅ Skip empty queues
                if (queueIds == null || queueIds.isEmpty()) {
                    System.out.println("ℹ No queue for user " + userId + " → skipping");
                    return;
                }

                Report.TestCase tc =
                        new Report.TestCase(
                                testData.getTestCases().get(0)
                        );

                tc.setTcId("DEL_QUEUE_" + projectId + "_" + userId);
                tc.setName("Delete Queue | Project " + projectId + " | User " + userId);

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

                            System.out.println(
                                    "🗑 Queue deleted → project=" + projectId +
                                            ", user=" + userId +
                                            ", queue=" + queueIds
                            );

                            return response;
                        }
                );
            });
        });
    }
}