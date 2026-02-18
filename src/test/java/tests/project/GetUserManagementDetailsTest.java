package tests.project;

import api.project.ProjectApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetUserManagementDetailsTest extends BaseTest {


    public void getUserManagementDetailsApiTest() {

        GetUserManagementDetailsTestData testData =
                JsonUtils.readJson(
                        "testdata/project/getUserManagementProjectById.json",
                        GetUserManagementDetailsTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            GetUserManagementDetailsTestData testData,
            List<GetUserManagementDetailsTestData.TestCase> cases
    ) {

        for (GetUserManagementDetailsTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                ProjectApi.getUserManagementDetailsForProjectId(
                                        tc.getRequest(),
                                        tc.getRole()
                                );

                        // ✅ Status code validation
                        Assert.assertEquals(
                                response.getStatusCode(),
                                tc.getExpectedStatusCode(),
                                "Status code mismatch"
                        );

                        // ✅ Message validation (if applicable)
                        if (tc.getExpectedMessage() != null) {
                            Assert.assertTrue(
                                    response.asString().toLowerCase()
                                            .contains(tc.getExpectedMessage().toLowerCase()),
                                    "Expected message not found"
                            );
                        }

                        // ✅ Response structure validation (200 OK)
                        if (tc.getExpectedStatusCode() == 200) {

                            Assert.assertNotNull(
                                    response.jsonPath().get("users"),
                                    "Users array should be present"
                            );

                            Assert.assertNotNull(
                                    response.jsonPath().get("roles"),
                                    "Roles should be present"
                            );

                            Assert.assertNotNull(
                                    response.jsonPath().get("permissions"),
                                    "Permissions should be present"
                            );
                        }

                        // ✅ Response time validation
                        if (tc.getMaxResponseTimeMs() != null) {
                            long time =
                                    response.timeIn(TimeUnit.MILLISECONDS);

                            Assert.assertTrue(
                                    time <= tc.getMaxResponseTimeMs(),
                                    "Response time exceeded limit: " + time
                            );
                        }

                        return response;
                    }
            );
        }
    }
}
