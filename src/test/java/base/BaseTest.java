package base;

import io.restassured.RestAssured;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import tests.login.LoginTest;
import utils.ConfigReader;
import utils.FailureTracker;

public class BaseTest {

    static {

        String activeEnv = ConfigReader.get("env.active");

        if (activeEnv == null || activeEnv.isBlank()) {
            throw new RuntimeException("❌ env.active not defined in config.properties");
        }

        String baseUrl;

        switch (activeEnv.toLowerCase()) {

            case "dev":
                baseUrl = ConfigReader.get("env.dev");
                break;

            case "test":
                baseUrl = ConfigReader.get("env.test");
                break;

            case "prod":
                baseUrl = ConfigReader.get("env.prod");
                break;

            case "devpw":
                baseUrl = ConfigReader.get("env.devpw");
                break;

            case "testpw":
                baseUrl = ConfigReader.get("env.testpw");
                break;

            case "testkb":
                baseUrl = ConfigReader.get("env.testkb");
                break;

            default:
                throw new RuntimeException("❌ Invalid env.active value → " + activeEnv);
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new RuntimeException("❌ Base URL missing for environment → " + activeEnv);
        }

        RestAssured.baseURI = baseUrl;
        RestAssured.useRelaxedHTTPSValidation();

        System.out.println("\n🌍 ACTIVE ENVIRONMENT → " + activeEnv.toUpperCase());
        System.out.println("🔗 BASE URL → " + baseUrl);
    }

    @BeforeSuite(alwaysRun = true)
    public void generateAuthToken() {

        if (LoginTest.authToken == null) {
            System.out.println("🔐 Generating authentication token...");
            new LoginTest().loginTest();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void showFailures() {

        if (FailureTracker.hasFailures()) {

            System.out.println("\n========= FAILED TEST SUMMARY =========");
            FailureTracker.getFailures().forEach(System.out::println);
            System.out.println("======================================");

        } else {
            System.out.println("\n✅ All test cases passed successfully!");
        }
    }
}
