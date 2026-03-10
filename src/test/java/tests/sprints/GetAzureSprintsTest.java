package tests.sprints;

import api.azure.GetAzureDevOpsSprintsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
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

        Report.TestCase tc = testData.getTestCases().get(0);

        Map<String, Object> request;

        if (tc.getRequest() != null) {
            request = new HashMap<>((Map<String, Object>) tc.getRequest());
        } else {
            request = new HashMap<>();
        }

        request.put("userId", String.valueOf(TokenUtil.getUserId()));
        request.put("refProjectId", ProjectStore.getSelectedProjectId());

        // ✅ Store payload in report
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

                    System.out.println("🔍 Azure API Response Received.");

                    if (response.getStatusCode() != 200) {
                        return response;
                    }

                    // ================== READ BOTH SCRUM + KANBAN ==================

                    List<Map<String, Object>> sprintsList =
                            response.jsonPath().getList("sprints");

                    List<Map<String, Object>> kanbanList =
                            response.jsonPath().getList("kanbanIterations");

                    List<Map<String, Object>> allIterations = new ArrayList<>();

                    if (sprintsList != null) allIterations.addAll(sprintsList);
                    if (kanbanList != null) allIterations.addAll(kanbanList);

                    if (allIterations.isEmpty()) {
                        throw new RuntimeException(
                                "❌ No sprints or kanban iterations returned from Azure"
                        );
                    }

                    System.out.println("========== AZURE SPRINT/ITERATION LIST ==========");
                    for (int i = 0; i < allIterations.size(); i++) {

                        Map<String, Object> s = allIterations.get(i);

                        System.out.println(
                                "[" + i + "] name=" + s.get("name")
                                        + ", iterationPath=" + s.get("iterationPath")
                        );
                    }

                    // ================== SMART ITERATION SELECTION ==================

                    Map<String, Object> selectedIteration = null;
                    int maxStoryCount = -1;

                    // -------- PHASE 1: Pick iteration that contains user stories --------
                    for (int i = allIterations.size() - 1; i >= 0; i--) {

                        Map<String, Object> iteration = allIterations.get(i);
                        String iterationPath =
                                (String) iteration.get("iterationPath");

                        // Skip ROOT node
                        if (iterationPath == null ||
                                !iterationPath.contains("\\")) {
                            continue;
                        }

                        SprintStore.setSelectedSprint(iterationPath);

                        int storyCount =
                                AzureSprintValidator.getUserStoryCountForSprint();

                        System.out.println(
                                "🔍 Path: " + iterationPath +
                                        " | User Story Count: " + storyCount
                        );

                        if (storyCount > 0 && storyCount > maxStoryCount) {
                            maxStoryCount = storyCount;
                            selectedIteration = iteration;
                        }
                    }

                    // -------- PHASE 2: FALLBACK → latest valid iteration --------
                    if (selectedIteration == null) {

                        for (int i = allIterations.size() - 1; i >= 0; i--) {

                            String path =
                                    (String) allIterations.get(i).get("iterationPath");

                            if (path != null && path.contains("\\")) {

                                selectedIteration = allIterations.get(i);

                                System.out.println(
                                        "⚠️ Using latest iteration fallback: " + path
                                );
                                break;
                            }
                        }
                    }

                    if (selectedIteration == null) {
                        throw new RuntimeException(
                                "❌ No valid Azure iteration could be selected"
                        );
                    }

                    String finalIterationPath =
                            (String) selectedIteration.get("iterationPath");

                    SprintStore.setSelectedSprint(finalIterationPath);

                    System.out.println(
                            "✅ FINAL SELECTED PATH: " + finalIterationPath
                    );

                    return response;
                }
        );
    }
}