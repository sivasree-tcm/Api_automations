package tests.user;

import api.UserManagement.UserManagementApi;
import base.BaseTest;
import org.testng.annotations.Test;
import utils.JsonUtils;

import java.util.List;

public class ToggleUserStatus extends BaseTest {


    public void toggleUserStatusApiTest() {

        ToggleUserStatusTestData testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/toggleUserStatus.json",
                        ToggleUserStatusTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            ToggleUserStatusTestData testData,
            List<ToggleUserStatusTestData.TestCase> cases
    ) {

        // ✅ CRITICAL: Prevent NullPointerException
        if (cases == null || cases.isEmpty()) {
            throw new RuntimeException(
                    "No test cases found for scenario: " + testData.getScenario()
            );
        }

        for (ToggleUserStatusTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> UserManagementApi.toggleUserStatus(
                            tc.getRequest(),   // Object request
                            tc.getRole()
                    )
            );
        }
    }
}
