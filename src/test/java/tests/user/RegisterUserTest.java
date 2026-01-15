package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.user.RegisterUserRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import report.CustomReportManager;
import report.ReportContext;
import report.ReportStep;
import report.ReportTest;
import utils.JsonUtils;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUserTest() {

        // 🔹 Start custom report test
        ReportTest reportTest =
                ReportContext.startTest("registerUserTest");

        try {
            // 🔹 LOAD REQUEST
            RegisterUserRequest request =
                    JsonUtils.readJson(
                            "testdata/project/registerUser.json",
                            RegisterUserRequest.class
                    );

            // 🔹 DYNAMIC DATA
            String dynamicEmail =
                    "demo_" + System.currentTimeMillis() + "@tickingminds.com";
            request.setUserEmailId(dynamicEmail);

            // 🔹 LOG STEPS
            reportTest.addStep(new ReportStep(
                    "Info", "HTTP Method", "POST"
            ));
            reportTest.addStep(new ReportStep(
                    "Info", "Endpoint", "/api/registerUser"
            ));
            reportTest.addStep(new ReportStep(
                    "Info", "Request Payload",
                    JsonUtils.toJson(request)
            ));

            // 🔹 API CALL
            Response response =
                    RegisterUserApi.registerUser(request);

            reportTest.addStep(new ReportStep(
                    "Info", "Actual Status Code",
                    String.valueOf(response.getStatusCode())
            ));
            reportTest.addStep(new ReportStep(
                    "Info", "Response",
                    response.asPrettyString()
            ));

            // 🔹 ASSERTIONS
            Assert.assertEquals(response.getStatusCode(), 201);
            Assert.assertEquals(
                    response.jsonPath().getString("status"),
                    "success"
            );

            // 🔹 PASS
            reportTest.markPassed("User registered successfully");

        } catch (AssertionError | Exception e) {

            // 🔹 FAIL
            reportTest.markFailed(e.getMessage());
            throw e;

        } finally {

            // 🔥 VERY IMPORTANT
            CustomReportManager.addTest(reportTest);
        }
    }
}
