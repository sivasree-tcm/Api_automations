package tests.sprints;

import api.azure.GetUserStoriesApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.SprintStore;
import utils.TokenUtil;
import utils.UserStoryStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetUserStoriesTest extends BaseTest {


     public void getUserStories() {

        // ✅ Read JSON test data
        var testData = JsonUtils.readJson(
                "testdata/project/getUserStories.json",
                tests.connection.ConnectionReport.class
        );

        var tc = testData.getTestCases().get(0);

        // ✅ Build request from JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> request =
                tc.getRequest() == null
                        ? new HashMap<>()
                        : new HashMap<>((Map<String, Object>) tc.getRequest());

        // ✅ Inject dynamic values
        request.put("userId", String.valueOf(TokenUtil.getUserId()));
        request.put("refProjectId", ProjectStore.getSelectedProjectId());
        request.put("sprintName", extractSprintName());
        request.put("platform", "azure");

        System.out.println("📦 GetUserStories Payload:");
        System.out.println(request);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetUserStoriesApi.getUserStories(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    // ✅ ADD THIS BLOCK (VERY IMPORTANT)
                    if (response.getStatusCode() == 200) {

                        List<Integer> userStoryIds =
                                response.jsonPath().getList("workItems.id");

                        if (userStoryIds == null || userStoryIds.isEmpty()) {
                            System.out.println("⚠️ No user stories found for sprint: " + extractSprintName());
                            UserStoryStore.clear();
                            return response;
                        }


                        // ✅ Store for Import API
                        UserStoryStore.setUserStoryIds(userStoryIds);

                        System.out.println("✅ User Stories Count: " + userStoryIds.size());
                        System.out.println("✅ User Story IDs: " + userStoryIds);
                    }

                    return response;
                }
        );
    }

    /**
     * Azure sprint iterationPath = CogniTestWorkbench\\Sprint6
     * API needs sprintName = Sprint6
     */
    private String extractSprintName() {

        String iterationPath = SprintStore.getSelectedSprint();

        if (iterationPath == null) {
            throw new RuntimeException("❌ Sprint not selected before GetUserStories");
        }

        if (iterationPath.contains("\\")) {
            return iterationPath.substring(iterationPath.lastIndexOf("\\") + 1);
        }

        return iterationPath;
    }
}