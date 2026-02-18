package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class GetProjectById extends BaseTest {




    public void getProjectByIdApiTest() {

        GetProjectByIdTestData testData =
                JsonUtils.readJson(
                        "testdata/project/GetProjectById.json",
                        GetProjectByIdTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            GetProjectByIdTestData testData,
            List<GetProjectByIdTestData.TestCase> cases
    ) {

        for (GetProjectByIdTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.getProjectById(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}
