package tests.sprints;

import api.azure.GetAzureDevOpsSprintsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAzureSprintsTest extends BaseTest {


    public void getAzureDevOpsSprints() {

        var testData = JsonUtils.readJson(
                "testdata/project/getAzureDevOpsSprints.json",
                tests.connection.ConnectionReport.class
        );

        var tc = testData.getTestCases().get(0);

        @SuppressWarnings("unchecked")
        Map<String, Object> request =
                tc.getRequest() == null
                        ? new HashMap<>()
                        : new HashMap<>((Map<String, Object>) tc.getRequest());

        request.put("userId", String.valueOf(TokenUtil.getUserId()));
        request.put("refProjectId", ProjectStore.getSelectedProjectId());

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

                    System.out.println("🔍 Azure Sprint API Response:");
                    System.out.println(response.asString());

                    if (response.getStatusCode() != 200) {
                        return response;
                    }

                    List<Map<String, Object>> sprints =
                            response.jsonPath().getList("sprints");

                    if (sprints == null || sprints.isEmpty()) {
                        throw new RuntimeException("❌ No sprints returned from Azure");
                    }

                    System.out.println("========== AZURE SPRINT LIST ==========");
                    for (int i = 0; i < sprints.size(); i++) {
                        Map<String, Object> s = sprints.get(i);
                        System.out.println(
                                "[" + i + "] " +
                                        "name=" + s.get("name") +
                                        ", iterationPath=" + s.get("iterationPath") +
                                        ", startDate=" + s.get("startDateForSprint")
                        );
                    }


                    // ================== CUSTOM SPRINT SELECTION LOGIC ==================

                    Map<String, Object> selectedSprint = null;
                    int maxBrCount = 0;

// ---------- PHASE 1: Prefer sprint with BR > 4 ----------
                    for (int i = sprints.size() - 1; i >= 0; i--) {

                        Map<String, Object> sprint = sprints.get(i);
                        String iterationPath = (String) sprint.get("iterationPath");

                        if (iterationPath == null || iterationPath.trim().isEmpty()) {
                            continue;
                        }

                        SprintStore.setSelectedSprint(iterationPath);
                        int brCount = AzureSprintValidator.getUserStoryCountForSprint();

                        System.out.println("🔍 Sprint: " + iterationPath + " | BR Count: " + brCount);

                        if (brCount > 4 && brCount >= maxBrCount) {
                            maxBrCount = brCount;
                            selectedSprint = sprint;
                        }
                    }

// ---------- PHASE 2: Fallback order (1st → 3rd → 2nd) ----------
                    if (selectedSprint == null) {

                        int[] fallbackOrder = {0, 2, 1};

                        for (int index : fallbackOrder) {
                            if (index >= sprints.size()) continue;

                            Map<String, Object> sprint = sprints.get(index);
                            String iterationPath = (String) sprint.get("iterationPath");

                            if (iterationPath == null || iterationPath.trim().isEmpty()) {
                                continue;
                            }

                            SprintStore.setSelectedSprint(iterationPath);
                            selectedSprint = sprint;

                            System.out.println("⚠️ Using fallback sprint: " + iterationPath);
                            break;
                        }
                    }

// ---------- PHASE 3: Absolute fallback ----------
                    if (selectedSprint == null && !sprints.isEmpty()) {
                        selectedSprint = sprints.get(sprints.size() - 1);
                        System.out.println("⚠️ Using last sprint as fallback");
                    }

// ---------- FINAL SELECTION ----------
                    if (selectedSprint == null) {
                        throw new RuntimeException("❌ No sprint could be selected");
                    }

                    String finalIterationPath =
                            (String) selectedSprint.get("iterationPath");

                    SprintStore.setSelectedSprint(finalIterationPath);

                    System.out.println("✅ FINAL SELECTED SPRINT: " + finalIterationPath);


                    return response;
                }
        );
    }
}
