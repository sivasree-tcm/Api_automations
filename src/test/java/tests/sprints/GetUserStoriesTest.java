package tests.sprints;

import api.azure.GetUserStoriesApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.user.ApiTestExecutor;
import utils.*;
import java.util.*;

public class GetUserStoriesTest extends BaseTest {

    public void getUserStories() {
        var testData = JsonUtils.readJson(
                "testdata/project/getUserStories.json",
                tests.connection.ConnectionReport.class
        );

        var tc = testData.getTestCases().get(0);

        Map<String, Object> baseRequest = tc.getRequest() == null
                ? new HashMap<>()
                : new HashMap<>((Map<String, Object>) tc.getRequest());

        // ✅ Ensure we use getSelectedProjectId() for dynamic targeting
        baseRequest.put("userId", String.valueOf(TokenUtil.getUserId()));
        baseRequest.put("refProjectId", ProjectStore.getSelectedProjectId());
        baseRequest.put("sprintName", extractSprintName());
        baseRequest.put("platform", "azure");

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {
                    int page = 1;
                    List<Integer> allUserStoryIds = new ArrayList<>();
                    Response lastResponse = null;

                    // Simple pagination loop
                    while (page <= 10) {
                        Map<String, Object> pagedRequest = new HashMap<>(baseRequest);
                        pagedRequest.put("page", page);
                        pagedRequest.put("pageSize", 50);

                        Response response = GetUserStoriesApi.getUserStories(pagedRequest, tc.getRole(), tc.getAuthType());
                        lastResponse = response;

                        if (response.getStatusCode() != 200) break;

                        List<Integer> ids = response.jsonPath().getList("workItems.id");
                        if (ids == null || ids.isEmpty()) break;

                        allUserStoryIds.addAll(ids);
                        page++;
                    }

                    if (allUserStoryIds.isEmpty()) {
                        System.out.println("⚠️ No User Stories found in iteration: " + extractSprintName());
                        UserStoryStore.clear();
                    } else {
                        UserStoryStore.setUserStoryIds(new ArrayList<>(new LinkedHashSet<>(allUserStoryIds)));
                        System.out.println("✅ Stored " + allUserStoryIds.size() + " User Stories.");
                    }

                    return lastResponse;
                }
        );
    }

    private String extractSprintName() {
        String path = SprintStore.getSelectedSprint();
        if (path == null) throw new RuntimeException("❌ No Sprint selected in Store.");
        return path.contains("\\") ? path.substring(path.lastIndexOf("\\") + 1) : path;
    }
}