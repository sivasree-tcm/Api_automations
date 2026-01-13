package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getExtent() {
        if (extent == null) {
            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("target/extent-report/ExtentReport.html");

            reporter.config().setReportName("API Automation Report");
            reporter.config().setDocumentTitle("API Test Execution");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }
        return extent;
    }
}
