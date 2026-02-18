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

import java.util.*;

public class GetUserStoriesTest extends BaseTest {


    public void getUserStories() {

        // ✅ Read JSON test data
        var testData = JsonUtils.readJson(
                "testdata/project/getUserStories.json",
                tests.connection.ConnectionReport.class
        );

        var tc = testData.getTestCases().get(0);

        // ✅ Base request from JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> baseRequest =
                tc.getRequest() == null
                        ? new HashMap<>()
                        : new HashMap<>((Map<String, Object>) tc.getRequest());

        // ✅ Inject dynamic values
        baseRequest.put("userId", String.valueOf(TokenUtil.getUserId()));
        baseRequest.put("refProjectId", ProjectStore.getSelectedProjectId());
        baseRequest.put("sprintName", extractSprintName());
        baseRequest.put("platform", "azure");

        System.out.println("📦 GetUserStories Base Payload:");
        System.out.println(baseRequest);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    int page = 1;
                    int pageSize = 20;
                    int maxPages = 50; // 🔒 safety limit

                    List<Integer> allUserStoryIds = new ArrayList<>();
                    List<Integer> previousPageIds = null;
                    Response lastResponse = null;

                    while (page <= maxPages) {

                        // 🔁 Build paginated request
                        Map<String, Object> pagedRequest = new HashMap<>(baseRequest);
                        pagedRequest.put("page", page);
                        pagedRequest.put("pageSize", pageSize);

                        Response response =
                                GetUserStoriesApi.getUserStories(
                                        pagedRequest,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        lastResponse = response;

                        if (response.getStatusCode() != 200) {
                            System.out.println("❌ Failed at page " + page);
                            break;
                        }

                        List<Integer> currentPageIds =
                                response.jsonPath().getList("workItems.id");

                        if (currentPageIds == null || currentPageIds.isEmpty()) {
                            System.out.println("ℹ No user stories returned. Stop pagination.");
                            break;
                        }

                        // 🛑 STOP if backend repeats same data
                        if (previousPageIds != null &&
                                new HashSet<>(currentPageIds)
                                        .equals(new HashSet<>(previousPageIds))) {

                            System.out.println(
                                    "ℹ Duplicate page detected at page " + page +
                                            ". Backend pagination ignored. Stopping."
                            );
                            break;
                        }

                        allUserStoryIds.addAll(currentPageIds);

                        System.out.println(
                                "✅ Page " + page +
                                        " fetched → User Stories: " + currentPageIds.size()
                        );

                        previousPageIds = currentPageIds;
                        page++;
                    }

                    // ✅ Final storage
                    if (allUserStoryIds.isEmpty()) {
                        System.out.println(
                                "⚠️ No user stories found for sprint: " + extractSprintName()
                        );
                        UserStoryStore.clear();
                    } else {
                        // Remove duplicates defensively
                        allUserStoryIds =
                                new ArrayList<>(new LinkedHashSet<>(allUserStoryIds));

                        UserStoryStore.setUserStoryIds(allUserStoryIds);

                        System.out.println(
                                "✅ Total Unique User Stories Stored: " + allUserStoryIds.size()
                        );
                        System.out.println(
                                "✅ User Story IDs: " + allUserStoryIds
                        );
                    }

                    return lastResponse;
                }
        );
    }

    /**
     * Azure iterationPath → SprintName
     * Example: CogniTestWorkbench\\Sprint7 → Sprint7
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
