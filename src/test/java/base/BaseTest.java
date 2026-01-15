package base;

import io.restassured.RestAssured;
import org.testng.annotations.*;
import report.CustomReportManager;
import utils.ExtentTestListener;
import utils.JsonReportListener;
import utils.RestAssuredExtentFilter;

import java.lang.reflect.Method;

@Listeners(JsonReportListener.class)
public class BaseTest {

    static {
        RestAssured.baseURI = "https://test.cognitest.ai";
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        // 1️⃣ Init Extent
        ExtentTestListener.initReport();

        // 2️⃣ Add RestAssured filter
        RestAssured.filters(new RestAssuredExtentFilter());
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest(Method method) {
        // 3️⃣ Create Extent test node
        ExtentTestListener.createTest(method.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        // 4️⃣ Flush Extent
        ExtentTestListener.flushReport();

        // 5️⃣ Generate custom JSON → HTML report
        CustomReportManager.writeReport();
    }

}
