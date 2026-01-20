package tests.login;

import api.login.LoginApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

public class LoginTest extends BaseTest {

    public static String authToken;

    @Test
    public void loginTest() {

        Response response = LoginApi.login(
                ConfigReader.get("superadmin.email"),
                ConfigReader.get("superadmin.password")
        );

        // ✅ Status Code Validation
        Assert.assertEquals(response.getStatusCode(), 200);

        // ✅ Header Validation (ADD HERE)
        Assert.assertNotNull(
                response.getHeader("Content-Type"),
                "Content-Type header should not be null"
        );

        Assert.assertTrue(
                response.getHeader("Content-Type").contains("application/json"),
                "Content-Type should be application/json"
        );

        // ✅ Auth Token Validation
        authToken = response.getHeader("Authorization");
        Assert.assertNotNull(
                authToken,
                "Authorization token should not be null"
        );
    }
}
