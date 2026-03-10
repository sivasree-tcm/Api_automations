package tests.user;

import api.userManagement.UserManagementApi;
import base.BaseTest;
import report.Report;
import utils.JsonUtils;

import java.util.List;

public class ToggleUserStatus extends BaseTest {

    public void toggleUserStatusApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/toggleUserStatus.json",
                        Report.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            Report testData,
            List<Report.TestCase> cases
    ) {

        // ✅ CRITICAL: Prevent NullPointerException
        if (cases == null || cases.isEmpty()) {
            throw new RuntimeException(
                    "No test cases found for scenario: " + testData.getScenario()
            );
        }

        for (Report.TestCase tc : cases) {

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