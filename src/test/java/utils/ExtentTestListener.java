package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentTestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // Initialize report ONCE
    public static void initReport() {
        if (extent == null) {
            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("target/extent-report/ExtentReport.html");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }
    }

    // Create test BEFORE each @Test
    public static void createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    // Flush AFTER suite
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
