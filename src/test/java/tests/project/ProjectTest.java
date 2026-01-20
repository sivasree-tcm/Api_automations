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

            // ✅ Handle null request (for auth test cases)
            if (request != null) {

                // 🔹 Dynamic project name
                if ("DYNAMIC".equalsIgnoreCase(request.getProjectName())) {
                    request.setProjectName(
                            "Automation_" + System.currentTimeMillis()
                    );
                }

                int userId = TokenUtil.getUserId();
                request.setUserId(String.valueOf(userId));
                request.setProjectCreatedBy(String.valueOf(userId));
            }

            // ✅ Execute ONE test case
            ApiTestExecutor.execute(
                    testData.getScenario(),   // ✔ scenario fixed
                    tc,
                    () -> ProjectApi.createProject(
                            tc.getRequest(),
                            tc.getRole()          // ✔ role comes from JSON
                    )
            );
        }
    }
}
