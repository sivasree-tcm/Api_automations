package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class EditProjectTest extends BaseTest {

    @Test
    public void editProjectApiTest() {

        EditProjectTestData testData =
                JsonUtils.readJson(
                        "testdata/project/editProject.json",
                        EditProjectTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            EditProjectTestData testData,
            List<EditProjectTestData.TestCase> cases
    ) {

        for (EditProjectTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.editProject(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}
