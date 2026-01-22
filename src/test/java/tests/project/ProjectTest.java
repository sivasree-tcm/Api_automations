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

        // 🔹 Generate a unique base timestamp for this test run
        String timestamp = String.valueOf(System.currentTimeMillis());

        // 🔹 Generate a unique project name for duplicate tests ONCE
        String duplicateProjectName = "DuplicateTest_" + timestamp;

        for (ProjectTestData.TestCase tc : cases) {

            ProjectRequest request = tc.getRequest();

            // ✅ Handle null request (for auth test cases)
            if (request != null) {

                // 🔹 Determine if role is special (NO_AUTH, INVALID_TOKEN, etc.)
                boolean isSpecialRole = tc.getRole() != null &&
                        (tc.getRole().equalsIgnoreCase("NO_AUTH") ||
                                tc.getRole().equalsIgnoreCase("INVALID_TOKEN") ||
                                tc.getRole().equalsIgnoreCase("INVALID_CONTENT_TYPE"));

                // 🔹 Handle userId based on test case requirement
                // Only replace {{userId}} placeholder, don't override empty/null values
                if (request.getUserId() != null && request.getUserId().contains("{{userId}}")) {
                    int userId = isSpecialRole ? TokenUtil.getUserId("SUPER_ADMIN") : TokenUtil.getUserId(tc.getRole());
                    request.setUserId(String.valueOf(userId));
                }

                // 🔹 Handle projectCreatedBy based on test case requirement
                // Only replace {{userId}} placeholder, don't override empty/null values
                if (request.getProjectCreatedBy() != null && request.getProjectCreatedBy().contains("{{userId}}")) {
                    int userId = isSpecialRole ? TokenUtil.getUserId("SUPER_ADMIN") : TokenUtil.getUserId(tc.getRole());
                    request.setProjectCreatedBy(String.valueOf(userId));
                }

                // 🔹 Handle projectName to ensure uniqueness
                if (request.getProjectName() != null) {
                    // Replace {{projectName}} with the same name for duplicate test cases
                    if (request.getProjectName().contains("{{projectName}}")) {
                        request.setProjectName(duplicateProjectName);
                    }
                    // Make all other project names unique by appending timestamp
                    else if (!request.getProjectName().isEmpty() &&
                            !request.getProjectName().contains("{{") &&
                            !request.getProjectName().contains("<script>") &&
                            !request.getProjectName().contains("' OR '")) {
                        // Don't modify injection test cases or already unique names
                        String originalName = request.getProjectName();
                        // Only append timestamp if it doesn't already have one
                        if (!originalName.matches(".*_\\d{13}.*")) {
                            request.setProjectName(originalName + "_" + timestamp);
                        }
                    }
                }
            }

            // ✅ Execute ONE test case
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.createProject(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}