package tests.azure;

import api.azure.GetAzureDevOpsSprintsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAzureSprintsTest extends BaseTest {

    public void getAzureDevOpsSprints() {

        Report testData = JsonUtils.readJson(
                "testdata/project/getAzureDevOpsSprints.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getAzureDevOpsSprints.json missing");
        }

        Report.TestCase tc = testData.getTestCases().get(0);

        Map<String, Object> request;

        if (tc.getRequest() != null) {
            request = new HashMap<>((Map<String, Object>) tc.getRequest());
        } else {
            request = new HashMap<>();
        }

        request.put("userId", TokenUtil.getUserId(tc.getRole()));
        request.put("refProjectId", ProjectStore.getSelectedProjectId());

        tc.setRequest(request);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetAzureDevOpsSprintsApi.getAzureDevOpsSprints(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response.getStatusCode() != 200) {
                        return response;
                    }

                    List<Map<String, Object>> sprintsList =
                            response.jsonPath().getList("sprints");

                    List<Map<String, Object>> kanbanList =
                            response.jsonPath().getList("kanbanIterations");

                    List<Map<String, Object>> allIterations = new ArrayList<>();

                    if (sprintsList != null) allIterations.addAll(sprintsList);
                    if (kanbanList != null) allIterations.addAll(kanbanList);

                    if (allIterations.isEmpty()) {
                        throw new RuntimeException("❌ No Azure iterations found");
                    }

                    System.out.println("========== AZURE ITERATIONS ==========");

                    for (int i = 0; i < allIterations.size(); i++) {

                        Map<String, Object> s = allIterations.get(i);

                        System.out.println(
                                "[" + i + "] "
                                        + s.get("name")
                                        + " | "
                                        + s.get("iterationPath")
                        );
                    }

                    Map<String, Object> selectedIteration = null;
                    int maxStoryCount = -1;

                    for (int i = allIterations.size() - 1; i >= 0; i--) {

                        Map<String, Object> iteration = allIterations.get(i);

                        String name = (String) iteration.get("name");
                        String iterationPath =
                                (String) iteration.get("iterationPath");

                        if (iterationPath == null || !iterationPath.contains("\\")) {
                            continue;
                        }

                        // 🚫 Skip specific sprint
                        if ("Test Sprint for ATS".equalsIgnoreCase(name)) {

                            System.out.println(
                                    "⏭️ Skipping Sprint: " + name
                            );

                            continue;
                        }

                        SprintStore.setSelectedSprint(iterationPath);

                        int storyCount =
                                AzureSprintValidator.getUserStoryCountForSprint();

                        System.out.println(
                                "🔍 Path: " + iterationPath +
                                        " | Story Count: " + storyCount
                        );

                        if (storyCount > maxStoryCount) {

                            maxStoryCount = storyCount;
                            selectedIteration = iteration;
                        }
                    }

                    if (selectedIteration == null) {
                        throw new RuntimeException(
                                "❌ No valid Azure iteration found"
                        );
                    }

                    String finalIterationPath =
                            (String) selectedIteration.get("iterationPath");

                    SprintStore.setSelectedSprint(finalIterationPath);

                    System.out.println(
                            "✅ FINAL SELECTED SPRINT: " + finalIterationPath
                    );

                    return response;
                }
        );
    }
}