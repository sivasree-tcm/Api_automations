package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class GetMyProjectsTest extends BaseTest {


    public void getMyProjectsApiTest() {

        GetMyProjectsTestData testData =
                JsonUtils.readJson(
                        "testdata/project/getMyProjects.json",
                        GetMyProjectsTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            GetMyProjectsTestData testData,
            List<GetMyProjectsTestData.TestCase> cases
    ) {

        for (GetMyProjectsTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.getMyProjects(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}
