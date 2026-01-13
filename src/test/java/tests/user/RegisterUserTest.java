package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.user.RegisterUserRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
import utils.ConfigReader;
import utils.ExtentTestListener;
import utils.JsonUtils;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUserTest() {

        RegisterUserRequest request =
                JsonUtils.readJson(
                        "testdata/project/registerUser.json",
                        RegisterUserRequest.class
                );

        String dynamicEmail = "demo_" + System.currentTimeMillis() + "@tickingminds.com";
        request.setUserEmailId(dynamicEmail);

        Response response = RegisterUserApi.registerUser(request);

        // 🔹 EXPECTED RESULT
        int expectedStatusCode = 201;

        // 🔹 LOGGING TO EXTENT
        ExtentTestListener.getTest().info("HTTP Method: POST");
        ExtentTestListener.getTest().info("Endpoint: /api/registerUser");

        ExtentTestListener.getTest().info(
                "<b>Request Payload:</b><pre>" +
                        JsonUtils.toJson(request) + "</pre>"
        );

        ExtentTestListener.getTest().info(
                "<b>Expected Status Code:</b> " + expectedStatusCode
        );

        ExtentTestListener.getTest().info(
                "<b>Actual Response:</b><pre>" +
                        response.asPrettyString() + "</pre>"
        );

        // 🔹 ASSERTIONS
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode);
        Assert.assertEquals(response.jsonPath().getString("status"), "success");
    }

}
