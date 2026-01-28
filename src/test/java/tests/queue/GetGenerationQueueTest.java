package tests.queue;

import api.queue.GetGenerationQueueApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class GetGenerationQueueTest extends BaseTest {

    // -------------------- DATA PROVIDER --------------------
    @DataProvider(name = "generationQueueProvider")
    public Object[][] generationQueueProvider() {

        if (ProjectStore.getAllProjectIds().isEmpty()) {
            throw new RuntimeException("❌ Project list is empty. Run GetProjects first.");
        }

        List<Object[]> data = new ArrayList<>();

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            List<Map<String, Object>> users =
                    ProjectUserStore.getUsers(projectId);

            if (users == null || users.isEmpty()) {
                continue;
            }

            for (Map<String, Object> user : users) {

                Integer userId = (Integer) user.get("userId");
                String firstName = (String) user.get("userFirstName");
                String lastName = (String) user.get("userLastName");

                data.add(new Object[]{
                        projectId,
                        userId,
                        firstName + " " + lastName
                });
            }
        }

        return data.toArray(new Object[0][0]);
    }

    // -------------------- TEST --------------------

    public void getGenerationQueue(
            Integer projectId,
            Integer userIdFromApi,
            String userName
    ) {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/generation/getGenerationQueue.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase baseTc =
                testData.getTestCases().get(0);

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(baseTc);

        tc.setTcId("TC_GEN_QUEUE_" + projectId + "_" + userIdFromApi);
        tc.setName("Get Generation Queue | User: " + userName);

        // ------------------ REQUEST ------------------
        Map<String, Object> request = new HashMap<>();
        request.put("userIdFromAPI", userIdFromApi);
        request.put("projectId", projectId);
        request.put("userId", String.valueOf(TokenUtil.getUserId()));
        request.put("userName", userName);
        request.put("projectName", ProjectStore.getProjectName(projectId));

        tc.setRequest(request);

        ApiTestExecutor.execute(
                testData.getScenario() + " | Project=" + projectId,
                tc,
                () -> {

                    Response response =
                            GetGenerationQueueApi.getGenerationQueue(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    List<Map<String, Object>> queueList =
                            response.jsonPath().getList("data");

                    // ✅ Case 1: No Queue
                    if (queueList == null || queueList.isEmpty()) {
                        System.out.println(
                                "✅ No generation queue for userId: " + userIdFromApi
                        );
                        return response;
                    }

                    // ✅ Case 2: Queue Exists
                    List<Integer> queueIds = new ArrayList<>();

                    for (Map<String, Object> q : queueList) {
                        queueIds.add((Integer) q.get("queueId"));
                    }

                    GenerationQueueStore.store(userIdFromApi, queueIds);

                    System.out.println(
                            "📌 Queue IDs for user " + userIdFromApi + " → " + queueIds
                    );

                    return response;
                }
        );
    }
    public void fetchGenerationQueue() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/queueData/getGenerationQueue.json",
                        ConnectionReport.class
                );

        for (Integer projectId : ProjectStore.getAllProjectIds()) {

            for (Map<String, Object> user :
                    ProjectUserStore.getUsers(projectId)) {

                Integer userId = (Integer) user.get("userId");

                ConnectionReport.TestCase tc =
                        new ConnectionReport.TestCase(
                                testData.getTestCases().get(0)
                        );

                Map<String, Object> request = new HashMap<>();
                request.put("projectId", projectId);
                request.put("userIdFromAPI", userId);
                request.put("userId", TokenUtil.getUserId());

                tc.setRequest(request);
                tc.setTcId("GEN_QUEUE_" + projectId + "_" + userId);
                tc.setName("Get Queue for User " + userId);

                ApiTestExecutor.execute(
                        testData.getScenario(),
                        tc,
                        () -> {

                            Response response =
                                    GetGenerationQueueApi.getGenerationQueue(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            List<Map<String, Object>> queue =
                                    response.jsonPath().getList("data");

                            // ✅ CASE 1: Queue is empty
                            if (queue == null || queue.isEmpty()) {

                                System.out.println(
                                        "ℹ No generation queue for user: " + userId
                                );

                                // Store empty list so delete step won't fail
                                GenerationQueueStore.store(
                                        userId,
                                        new ArrayList<>()
                                );

                                return response;
                            }

                            // ✅ CASE 2: Queue exists
                            List<Integer> queueIds =
                                    queue.stream()
                                            .map(q -> (Integer) q.get("queueId"))
                                            .toList();

                            GenerationQueueStore.store(userId, queueIds);

                            System.out.println(
                                    "✅ Queue found for user " + userId +
                                            " → " + queueIds
                            );


                            return response;
                        }
                );
            }
        }
    }
}
