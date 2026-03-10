package tests.project;

import api.project.GetMyProjectsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetMyProjectsTest extends BaseTest {

    public void getMyProjectsApiTest() {

        String userId = String.valueOf(TokenUtil.getUserId());

        Report testData =
                JsonUtils.readJson(
                        "testdata/project/getMyProjects.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getMyProjects.json missing or invalid.");
        }

        Report.TestCase tc =
                testData.getTestCases().get(0);

        Map<String, Object> request = new HashMap<>();

        request.put("userId", userId);
        request.put("isSuperAdmin", true);

        // ✅ Include payload in report
        tc.setRequest(request);

        tc.setTcId("GET_MY_PROJECTS_" + userId);
        tc.setName("Get My Projects for User " + userId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetMyProjectsApi.getMyProjects(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch projects. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 Get My Projects Response → ");
                    System.out.println(response.asPrettyString());

                    List<Map<String, Object>> projects =
                            response.jsonPath().getList("projects");

                    if (projects == null || projects.isEmpty()) {
                        throw new RuntimeException("❌ No projects returned in response.");
                    }

                    System.out.println("✅ Total Projects Found → " + projects.size());

                    return response;
                }
        );
    }
}