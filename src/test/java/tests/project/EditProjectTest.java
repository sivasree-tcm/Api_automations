package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class EditProjectTest extends BaseTest {

    private static final String PROJECT_ID = "230";
    private static final String USER_ID = "33";

    @Test
    public void editProjectApiTest() {

        EditProjectTestData testData =
                JsonUtils.readJson(
                        "testdata/project/EditProject.json",
                        EditProjectTestData.class
                );

        // Generate dynamic project name with timestamp
        String dynamicProjectName = generateDynamicProjectName();

        // Replace placeholders in test data
        replacePlaceholders(testData, dynamicProjectName);

        execute(testData, testData.getTestCases());
    }

    private String generateDynamicProjectName() {
        long timestamp = System.currentTimeMillis();
        return "AutoProject_" + timestamp;
    }

    private void replacePlaceholders(
            EditProjectTestData testData,
            String projectName
    ) {
        // Replace in base payload


        // Replace in all test cases
        if (testData.getTestCases() != null) {
            for (EditProjectTestData.TestCase tc : testData.getTestCases()) {
                if (tc.getRequest() != null) {
                    replaceInRequest(tc.getRequest(), projectName);
                }
            }
        }
    }

    private void replaceInRequest(Object request, String projectName) {
        try {
            // Use reflection to replace placeholders in request object
            Class<?> clazz = request.getClass();

            // Replace projectName
            try {
                var nameField = clazz.getDeclaredField("projectName");
                nameField.setAccessible(true);
                String currentName = (String) nameField.get(request);
                if (currentName != null && currentName.contains("{{timestamp}}")) {
                    nameField.set(request, currentName.replace("{{timestamp}}",
                            String.valueOf(System.currentTimeMillis())));
                }
            } catch (NoSuchFieldException e) {
                // Field might not exist in this request, ignore
            }

            // Replace projectId
            try {
                var idField = clazz.getDeclaredField("projectId");
                idField.setAccessible(true);
                String currentId = (String) idField.get(request);
                if (currentId != null && currentId.contains("{{projectId}}")) {
                    idField.set(request, PROJECT_ID);
                }
            } catch (NoSuchFieldException e) {
                // Field might not exist in this request, ignore
            }

            // Replace userId
            try {
                var userIdField = clazz.getDeclaredField("userId");
                userIdField.setAccessible(true);
                String currentUserId = (String) userIdField.get(request);
                if (currentUserId != null && currentUserId.contains("{{userId}}")) {
                    userIdField.set(request, USER_ID);
                }
            } catch (NoSuchFieldException e) {
                // Field might not exist in this request, ignore
            }

            // Replace projectCreatedBy
            try {
                var createdByField = clazz.getDeclaredField("projectCreatedBy");
                createdByField.setAccessible(true);
                String currentCreatedBy = (String) createdByField.get(request);
                if (currentCreatedBy != null && currentCreatedBy.contains("{{userId}}")) {
                    createdByField.set(request, USER_ID);
                }
            } catch (NoSuchFieldException e) {
                // Field might not exist in this request, ignore
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error replacing placeholders in request", e);
        }
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