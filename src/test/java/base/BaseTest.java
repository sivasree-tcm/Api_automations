package base;

import io.restassured.RestAssured;
import io.qameta.allure.testng.AllureTestNg;
import org.codehaus.groovy.syntax.TokenUtil;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import utils.RestAssuredExtentFilter;

@Listeners(utils.ExtentTestListener.class)


public class BaseTest {


    @BeforeSuite
    public void setup() {
        RestAssured.filters(new RestAssuredExtentFilter());
    }

    static {
        RestAssured.baseURI = "https://test.cognitest.ai";
    }
}
