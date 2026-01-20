package base;

import io.restassured.RestAssured;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import tests.login.LoginTest;
import utils.FailureTracker;

public class BaseTest {

    static {
        RestAssured.baseURI = "https://test.cognitest.ai";
    }

//    @BeforeSuite(alwaysRun = true)
//    public void generateAuthToken() {
//        if (LoginTest.authToken == null) {
//            LoginTest loginTest = new LoginTest();
//            loginTest.loginTest();
//        }
//    }
//
//    @AfterSuite
//    public void showFailures() {
//
//        if (FailureTracker.hasFailures()) {
//            System.out.println("\n========= FAILED TEST SUMMARY =========");
//
//            FailureTracker.getFailures().forEach(System.out::println);
//
//            System.out.println("======================================");
//        } else {
//            System.out.println("\n✅ All test cases passed successfully!");
//        }
//    }
}
