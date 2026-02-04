package tests.sprints;

import api.azure.ImportAzureUserStoriesApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;
import utils.UserStoryStore;

import java.util.HashMap;
import java.util.Map;

public class ImportAzureUserStoriesTest extends BaseTest {


    public void importAzureUserStories() {

        // ✅ Read JSON test data
        var testData = JsonUtils.readJson(
                "testdata/project/importUserStoriesAzure.json",
                tests.connection.ConnectionReport.class
        );

        var tc = testData.getTestCases().get(0);

        if (!UserStoryStore.hasStories()) {
            throw new RuntimeException("❌ No user stories available to import");
        }

        // ✅ Build request from JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> request =
                tc.getRequest() == null
                        ? new HashMap<>()
                        : new HashMap<>((Map<String, Object>) tc.getRequest());

        // ✅ Inject dynamic values
        request.put("userStoryId", UserStoryStore.getUserStoryIds());
        request.put("refProjectId", ProjectStore.getSelectedProjectId());
        request.put("userId", String.valueOf(TokenUtil.getUserId()));

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

                    return response;
                }
        );
    }
}