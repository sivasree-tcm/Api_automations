package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import io.restassured.response.Response;
import models.user.RegisterUserRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;
import utils.ConfigReader;
import utils.JsonUtils;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUserTest() {

        String endpoint = ConfigReader.get("endpoint.registerUser");
        String executionStatus = "FAIL";

        // ✅ Load payload from JSON
        RegisterUserRequest request =
                JsonUtils.readJson(
                        "testdata/project/registerUser.json",
                        RegisterUserRequest.class
                );

        // ✅ Dynamic replacement
        String dynamicEmail =
                "demo_" + System.currentTimeMillis() + "@tickingminds.com";
        request.setUserEmailId(dynamicEmail);

        Response response = RegisterUserApi.registerUser(request);

        try {
            // ✅ Assertions
            Assert.assertEquals(response.getStatusCode(), 201);
            Assert.assertEquals(response.jsonPath().getString("status"), "success");
            Assert.assertTrue(response.jsonPath().getInt("user_id") > 0);

            executionStatus = "PASS";

        } finally {
            // ✅ Allure Reporting (Fully Dynamic)
            AllureUtil.attachText("Endpoint", endpoint);
            AllureUtil.attachJson("Request Payload", JsonUtils.toJson(request));
            AllureUtil.attachJson("Actual Response", response.asPrettyString());
            AllureUtil.attachText("Execution Status", executionStatus);
        }

        System.out.println("User registered successfully: " + dynamicEmail);
    }
}
