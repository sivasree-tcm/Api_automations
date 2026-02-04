package utils;

import api.azure.GetUserStoriesApi;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class AzureSprintValidator {

    public static int getUserStoryCountForSprint() {

        String iterationPath = SprintStore.getSelectedSprint();

        if (iterationPath == null) {
            return 0;
        }

        String sprintName = iterationPath.contains("\\")
                ? iterationPath.substring(iterationPath.lastIndexOf("\\") + 1)
                : iterationPath;

        Map<String, Object> request = new HashMap<>();
        request.put("userId", String.valueOf(TokenUtil.getUserId()));
        request.put("refProjectId", ProjectStore.getSelectedProjectId());
        request.put("platform", "azure");
        request.put("sprintName", sprintName);
        request.put("selectedStoryType", new String[]{"User Story"});

        Response response =
                GetUserStoriesApi.getUserStories(
                        request,
                        "SUPER_ADMIN",
                        "VALID"
                );

        if (response.getStatusCode() != 200) {
            return 0;
        }

        var stories = response.jsonPath().getList("workItems");
        return stories == null ? 0 : stories.size();
    }
}