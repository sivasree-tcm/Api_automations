package base;

import io.restassured.RestAssured;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class})


public class BaseTest {

    static {
        RestAssured.baseURI = "https://test.cognitest.ai";
    }
}
