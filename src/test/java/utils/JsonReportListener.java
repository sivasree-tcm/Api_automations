//package utils;
//
//import org.testng.ITestListener;
//import org.testng.ITestResult;
//import report.CustomReportManager;
//import report.ReportTest;
//
//public class JsonReportListener implements ITestListener {
//
//    @Override
//    public void onTestStart(ITestResult result) {
//
//        // ✅ MUST pass test name
//        ReportTest test =
//                new ReportTest(result.getMethod().getMethodName());
//
//        // store in TestNG context
//        result.setAttribute("reportTest", test);
//        result.setAttribute("startTime", System.currentTimeMillis());
//    }
//
//    @Override
//    public void onTestSuccess(ITestResult result) {
//        finishTest(result, "PASS", null);
//    }
//
//    @Override
//    public void onTestFailure(ITestResult result) {
//        finishTest(
//                result,
//                "FAIL",
//                result.getThrowable() != null
//                        ? result.getThrowable().getMessage()
//                        : "Test failed"
//        );
//    }
//
//    private void finishTest(
//            ITestResult result,
//            String status,
//            String errorMessage
//    ) {
//        ReportTest test =
//                (ReportTest) result.getAttribute("reportTest");
//
//        long start =
//                (long) result.getAttribute("startTime");
//
//        long duration =
//                System.currentTimeMillis() - start;
//
//        if ("PASS".equals(status)) {
//            test.markPassed("Test passed");
//        } else {
//            test.markFailed(errorMessage);
//        }
//
//        // ✅ ADD TO CUSTOM REPORT
//        CustomReportManager.addTest(test);
//    }
//}
