package tests.project;

import api.project.GetProjectDetailsApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class GetProjectDetailsTest extends BaseTest {

    public void fetchProjectDetails(Integer expectedProjectId) {

        if (expectedProjectId == null) {
            throw new RuntimeException("❌ Expected Project ID is null.");
        }

        ProjectStore.setSelectedProject(expectedProjectId);
        ProjectFileLogger.logSelectedProject();

        System.out.println("✅ Selected Project ID set → " + expectedProjectId);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/GetProjectDetails.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ GetProjectDetails.json missing or invalid.");
        }

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        Map<String, Object> request = new HashMap<>();
        request.put("userId", TokenUtil.getUserId());
        request.put("projectId", expectedProjectId);

        tc.setRequest(request);
        tc.setTcId("GET_PROJECT_DETAILS_" + expectedProjectId);
        tc.setName("Get Project Details - " + expectedProjectId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response = GetProjectDetailsApi.getProjectDetails(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    );

                    if (response == null) {
                        throw new RuntimeException("❌ Project details API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch project details. Status → " +
                                        response.getStatusCode()
                        );
                    }

                    System.out.println("📦 Project Details Response → ");
                    System.out.println(response.asPrettyString());

                    // ✅ EXACT FIX BASED ON REAL RESPONSE
                    String projectName =
                            response.jsonPath().getString("results[0].projectName");

                    String storageType =
                            response.jsonPath().getString("results[0].storageType");

                    if (projectName == null || projectName.isBlank()) {
                        throw new RuntimeException("❌ projectName missing in response.");
                    }

                    if (storageType == null || storageType.isBlank()) {
                        throw new RuntimeException("❌ storageType missing in response.");
                    }

                    // ✅ Persist into ProjectStore
                    ProjectStore.updateProject(expectedProjectId, projectName);
                    ProjectStore.setProjectName(projectName);
                    ProjectStore.setStorageType(storageType);

                    System.out.println("✅ Project Name Stored → " + projectName);
                    System.out.println("✅ Storage Type Stored → " + storageType);

                    return response;
                }
        );
    }
}
