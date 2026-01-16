package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.user.RegisterUserRequest;
import tests.user.RegisterUserTestData;
import org.testng.Assert;
import org.testng.annotations.Test;
import report.CustomReportManager;
import report.ReportContext;
import report.ReportStep;
import report.ReportTest;
import utils.JsonUtils;

import java.util.List;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUserTest() {

        // ✅ Parent test (ONLY ONE ROW IN REPORT)
        ReportTest parentTest =
                ReportContext.startTest("registerUserTest");

        RegisterUserTestData testData =
                JsonUtils.readJson(
                        "testdata/project/registerUser.json",
                        RegisterUserTestData.class
                );

        runTestCases(parentTest, testData.getPositiveTestCases());
        runTestCases(parentTest, testData.getNegativeTestCases());

        // 🔥 Add ONLY parent test
        CustomReportManager.addTest(parentTest);
    }

    private void runTestCases(
            ReportTest parentTest,
            List<RegisterUserTestData.TestCase> cases
    ) {

        for (RegisterUserTestData.TestCase tc : cases) {

            long startTime = System.currentTimeMillis();

            try {
                RegisterUserRequest request = tc.getRequest();

                // 🔹 Dynamic Email Handling
                if ("DYNAMIC".equals(request.getUserEmailId())) {
                    request.setUserEmailId(
                            "demo_" + System.currentTimeMillis() + "@tickingminds.com"
                    );
                }

                parentTest.addStep(new ReportStep(
                        "Info",
                        "Test Case",
                        tc.getName()
                ));

                parentTest.addStep(new ReportStep(
                        "Info",
                        "Request Payload",
                        JsonUtils.toJson(request)
                ));

                Response response =
                        RegisterUserApi.registerUser(request);

                parentTest.addStep(new ReportStep(
                        "Info",
                        "Response",
                        response.asPrettyString()
                ));

                Assert.assertEquals(
                        response.getStatusCode(),
                        tc.getExpectedStatusCode()
                );

                parentTest.addStep(new ReportStep(
                        "Pass",
                        tc.getName(),
                        "Passed in " + (System.currentTimeMillis() - startTime) + " ms"
                ));

            } catch (AssertionError | Exception e) {

                parentTest.addStep(new ReportStep(
                        "Fail",
                        tc.getName(),
                        e.getMessage()
                ));
            }
        }
    }
}
