package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import models.user.RegisterUserRequest;
import org.testng.annotations.Test;
import utils.JsonUtils;

import java.util.List;

public class RegisterUserTest extends BaseTest {

    @Test
    public void registerUserTest() {

        RegisterUserTestData testData =
                JsonUtils.readJson(
                        "testdata/project/registerUser.json",
                        RegisterUserTestData.class
                );

        execute(testData, testData.getPositiveTestCases());
        execute(testData, testData.getNegativeTestCases());
    }

    private void execute(
            RegisterUserTestData testData,
            List<RegisterUserTestData.TestCase> cases
    ) {

        for (RegisterUserTestData.TestCase tc : cases) {

            RegisterUserRequest request = tc.getRequest();

            if ("DYNAMIC".equals(request.getUserEmailId())) {
                request.setUserEmailId(
                        "demo_" + System.currentTimeMillis() + "@mail.com"
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),   // ✅ FIX
                    tc,
                    () -> RegisterUserApi.registerUser(tc.getRequest())
            );
        }
    }
}
