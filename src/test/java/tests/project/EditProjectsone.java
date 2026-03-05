package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditProjectsone extends BaseTest {

    public void editProjectApiTest() {
        // 1. Load the JSON template
        EditProjectTestData testData = JsonUtils.readJson(
                "testdata/project/EditProjectone.json",
                EditProjectTestData.class
        );

        // 2. Prepare dynamic data
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nextMonth = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 3. Iterate through test cases and inject data from ProjectStore
        for (EditProjectTestData.TestCase tc : testData.getTestCases()) {
            Object request = tc.getRequest();

            // Inject values from ProjectStore into the request object
            mapStoreToRequest(request, today, nextMonth);

            // 4. Execute the API
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> ProjectApi.editProject(tc.getRequest(), tc.getRole())
            );
        }
    }

    private void mapStoreToRequest(Object request, String startDate, String endDate) {
        try {
            Class<?> clazz = request.getClass();

            // Mapping ProjectStore data to the Request Fields
            setFieldValue(clazz, request, "projectId", ProjectStore.getSelectedProjectId().toString());
            setFieldValue(clazz, request, "userId", ProjectStore.getUserId());
            setFieldValue(clazz, request, "projectCreatedBy", ProjectStore.getUserId());
            setFieldValue(clazz, request, "projectName", ProjectStore.getSelectedProjectName());
            setFieldValue(clazz, request, "storageType", ProjectStore.getStorageType());
            setFieldValue(clazz, request, "webFramework", ProjectStore.getAutomationFramework());

            // Injecting Dynamic Dates
            setFieldValue(clazz, request, "projectStartDate", startDate);
            setFieldValue(clazz, request, "projectEndDate", endDate);

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to map ProjectStore to Request object", e);
        }
    }

    private void setFieldValue(Class<?> clazz, Object instance, String fieldName, String value) {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException e) {
            // Field not present in this specific request variant, skip safely
        } catch (IllegalAccessException e) {
            System.err.println("Could not set field: " + fieldName);
        }
    }
}