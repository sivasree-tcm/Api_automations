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
                ConfigReader.get("login.email"),
                ConfigReader.get("login.password")
        );

        response.then().statusCode(200);

        authToken = response.getHeader("Authorization");
        Assert.assertNotNull(authToken, "Auth token should not be null");
    }
}
