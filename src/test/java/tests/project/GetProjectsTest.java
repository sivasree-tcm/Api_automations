package tests.project;

import api.project.GetProjectsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.util.List;
import java.util.Map;

public class GetProjectsTest extends BaseTest {


    public void getProjects() {

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/project/getProjects.json",
                ConnectionReport.class
        );

        ApiTestExecutor.execute(
                testData.getScenario(),
                testData.getTestCases().get(0),
                () -> {

                    Response response =
                            GetProjectsApi.getProjects(
                                    testData.getTestCases().get(0).getRequest(),
                                    testData.getTestCases().get(0).getRole(),
                                    testData.getTestCases().get(0).getAuthType()
                            );
                    if (response == null) {
                        throw new RuntimeException("API call failed: response is null");
                    }

                    // ✅ Store projectId + projectName
                    List<Map<String, Object>> projects =
                            response.jsonPath().getList("$");

                    ProjectStore.storeProjects(projects);

                    return response;
                }
        );
    }

    public void fetchProjects() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getProjects.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                testData.getTestCases().get(0);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetProjectsApi.getProjects(
                                    tc.getRequest(),
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    List<Map<String, Object>> projects =
                            response.jsonPath().getList("$");

                    ProjectStore.storeProjects(projects);

                    return response;
                }
        );
    }
}
