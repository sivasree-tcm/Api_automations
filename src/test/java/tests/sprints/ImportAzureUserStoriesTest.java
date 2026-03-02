package tests.sprints;

import api.azure.ImportAzureUserStoriesApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;
import utils.UserStoryStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportAzureUserStoriesTest extends BaseTest {

    public void importAzureUserStories() {

        // ✅ 1. Check Store BEFORE reading JSON or building request
        List<Integer> storyIds = UserStoryStore.getUserStoryIds();

        if (storyIds == null || storyIds.isEmpty()) {
            // ℹ️ We print a message and exit gracefully instead of throwing a RuntimeException.
            // This allows 'newprojectflow' to continue to the next @Test step.
            System.out.println("⏭️ Skipping Step: No user stories found in the selected Azure iteration to import.");
            return;
        }

        // ✅ 2. Read JSON test data
        var testData = JsonUtils.readJson(
                "testdata/project/importUserStoriesAzure.json",
                tests.connection.ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            System.out.println("❌ Import test data is missing or corrupted.");
            return;
        }

        var tc = testData.getTestCases().get(0);

        // ✅ 3. Build request dynamically
        Map<String, Object> request = new HashMap<>();

        // Use the ID list from UserStoryStore (Step 17)
        request.put("azureStoryIds", storyIds);
        request.put("refProjectId", ProjectStore.getSelectedProjectId());
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

        // ✅ 4. Execute API Call
        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {
                    Response response = ImportAzureUserStoriesApi.importStories(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    );

                    if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                        System.out.println("✅ Successfully imported " + storyIds.size() + " user stories.");
                    }

                    return response;
                }
        );
    }
}