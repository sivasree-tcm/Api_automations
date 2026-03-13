package tests.azure;

import api.azure.ImportAzureUserStoriesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;
import utils.UserStoryStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportAzureUserStoriesTest extends BaseTest {

    public void importAzureUserStories() {

        // Step 1: Check UserStoryStore before proceeding
        List<Integer> storyIds = UserStoryStore.getUserStoryIds();

        if (storyIds == null || storyIds.isEmpty()) {
            System.out.println("⏭️ Skipping Step: No user stories found in the selected Azure iteration to import.");
            return;
        }

        // Step 2: Read JSON test data
        Report testData = JsonUtils.readJson(
                "testdata/project/importUserStoriesAzure.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            System.out.println("❌ Import test data is missing or corrupted.");
            return;
        }

        Report.TestCase tc = testData.getTestCases().get(0);

        // Step 3: Load request from JSON (preserve fields like platform)
        Map<String, Object> request =
                (tc.getRequest() != null)
                        ? new HashMap<>((Map<String, Object>) tc.getRequest())
                        : new HashMap<>();

        // Step 4: Inject dynamic fields
        request.put("userStoryId", storyIds);
        request.put("refProjectId", ProjectStore.getSelectedProjectId());
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

        // Store payload for reporting
        tc.setRequest(request);

        System.out.println("FINAL REQUEST → " + request);

        // Step 5: Execute API
        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            ImportAzureUserStoriesApi.importStories(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    System.out.println("STATUS → " + response.getStatusCode());
                    System.out.println("BODY → " + response.asString());

                    if (response.getStatusCode() == 200 ||
                            response.getStatusCode() == 201) {

                        System.out.println(
                                "✅ Successfully imported "
                                        + storyIds.size()
                                        + " user stories."
                        );
                    }

                    return response;
                }
        );
    }
}