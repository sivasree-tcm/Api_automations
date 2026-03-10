package tests.project;

import api.project.GetProjectByIdApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class GetProjectByIdTest extends BaseTest {

    public void getProjectByIdApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/project/GetProjectById.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ GetProjectById.json missing or invalid.");
        }

        Report.TestCase tc =
                new Report.TestCase(
                        testData.getTestCases().get(0)
                );

        Map<String, Object> request;

        if (tc.getRequest() != null) {
            request = new HashMap<>((Map<String, Object>) tc.getRequest());
        } else {
            request = new HashMap<>();
        }

        // ✅ Dynamic injection
        request.put("projectId", ProjectStore.getSelectedProjectId());
        request.put("userId", TokenUtil.getUserId(tc.getRole()));

        // ✅ Store payload in report
        tc.setRequest(request);

        tc.setTcId("GET_PROJECT_BY_ID_" + ProjectStore.getProjectId());
        tc.setName("Get Project By ID | ProjectId = " + ProjectStore.getProjectId());

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetProjectByIdApi.getProjectById(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch project by ID. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 GetProjectById Response → ");
                    System.out.println(response.asPrettyString());

                    return response;
                }
        );
    }
}