package tests.project;

import api.project.GetProjectsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;

import java.util.List;
import java.util.Map;

public class GetProjectsTest extends BaseTest {

    public void fetchProjects() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/project/getProjects.json",
                        Report.class
                );

        Report.TestCase tc =
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