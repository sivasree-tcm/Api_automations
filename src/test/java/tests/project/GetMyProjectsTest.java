package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

public class GetMyProjectsTest extends BaseTest {

    @Test
    public void getMyProjectsTest() {

        GetMyProjectsTestData testData =
                JsonUtils.readJson(
                        "testdata/project/getMyProjects.json",
                        GetMyProjectsTestData.class
                );

        for (GetMyProjectsTestData.TestCase tc
                : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    ProjectApi::getMyProjects
            );
        }
    }
}
