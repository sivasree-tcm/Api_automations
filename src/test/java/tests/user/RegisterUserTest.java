package tests.user;

import api.user.RegisterUserApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterUserTest extends BaseTest {

    public void registerUserTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/registerUser.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ registerUser.json missing.");
        }

        Report.TestCase tc =
                testData.getTestCases().get(0);

        Integer orgId = OrganizationStore.getOrgId();

        String firstName = TestDataGenerator.randomFirstName();
        String lastName  = TestDataGenerator.randomLastName();
        String email = TestDataGenerator.generateEmail(firstName, lastName);
        String password = TestDataGenerator.generateValidPassword();
        String phone = "9" + (long)(Math.random()*1000000000L);

        Map<String,Object> request = new HashMap<>();

        request.put("userFirstName", firstName);
        request.put("userLastName", lastName);
        request.put("userEmailId", email);
        request.put("userPassword", password);
        request.put("userPhoneNumber", phone);
        request.put("userPhoneCountryCode", "91");
        request.put("organizationId", orgId);

        // ✔ Payload in report
        tc.setRequest(request);

        tc.setTcId("REGISTER_USER_" + System.currentTimeMillis());
        tc.setName("Register Dynamic User");

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            RegisterUserApi.registerUser(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if(response == null){
                        throw new RuntimeException("❌ API returned null response");
                    }

                    System.out.println("📦 Register User Response → ");
                    System.out.println(response.asPrettyString());

                    return response;
                }
        );
    }
}