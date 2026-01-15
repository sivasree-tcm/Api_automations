package utils;

public class ExtentTableUtil {

    public static void logTestCaseAsTable(
            String testCaseId,
            String scenario,
            String api,
            String method,
            String expected,
            String actual,
            String status
    ) {

        String color = status.equalsIgnoreCase("PASS")
                ? "green"
                : "red";

        String table =
                "<table border='1' cellpadding='5' cellspacing='0' width='100%'>" +
                        "<tr style='background-color:#f2f2f2'>" +
                        "<th>Test Case ID</th>" +
                        "<th>Scenario</th>" +
                        "<th>API</th>" +
                        "<th>Method</th>" +
                        "<th>Expected Result</th>" +
                        "<th>Actual Result</th>" +
                        "<th>Status</th>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>" + testCaseId + "</td>" +
                        "<td>" + scenario + "</td>" +
                        "<td>" + api + "</td>" +
                        "<td>" + method + "</td>" +
                        "<td>" + expected + "</td>" +
                        "<td>" + actual + "</td>" +
                        "<td style='color:" + color + ";font-weight:bold'>" +
                        status +
                        "</td>" +
                        "</tr>" +
                        "</table>";

        ExtentTestListener.getTest().info(table);
    }
}
