package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import models.project.ProjectRequest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.List;

public class ProjectTest extends BaseTest {

    @Test
    public void projectApiTest() {

        ProjectTestData testData =
                JsonUtils.readJson(
                        "testdata/project/createProject.json",
                        ProjectTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            ProjectTestData testData,
            List<ProjectTestData.TestCase> cases
    ) {

        for (ProjectTestData.TestCase tc : cases) {

            ProjectRequest request = tc.getRequest();

            // 🔹 Apply dynamic values (same style as RegisterUserTest)
            if ("DYNAMIC".equalsIgnoreCase(request.getProjectName())) {
                request.setProjectName(
                        "Automation_" + System.currentTimeMillis()
                );
            }

            int userId = TokenUtil.getUserId();
            request.setUserId(String.valueOf(userId));
            request.setProjectCreatedBy(String.valueOf(userId));

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.createProject(tc.getRequest())
            );
        }
    }
}
