package tests.user;

import io.restassured.response.Response;
import org.testng.Assert;
import report.CustomReportManager;
import report.ReportContext;
import report.ReportStep;
import report.ReportTest;
import tests.project.GetMyProjectsTestData;
import utils.JsonUtils;

import java.util.List;
import java.util.function.Supplier;

public class ApiTestExecutor {

    private static boolean hasMethod(Object obj, String methodName) {
        try {
            obj.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /* ======================================================
       GENERIC EXECUTOR (POST / PUT / DOWNLOAD SAFE)
       ====================================================== */
    public static <T> void execute(
            String scenarioName,
            T tc,
            Supplier<Response> apiCall
    ) {

        ReportTest test =
                new ReportTest(scenarioName + " :: " + tc.toString());

        ReportContext.setTest(test);
        long start = System.currentTimeMillis();

        try {

            /* ✅ REQUEST LOGGING (SAFE) */
            try {
                Object request =
                        tc.getClass().getMethod("getRequest").invoke(tc);

                test.addStep(new ReportStep(
                        "Info",
                        "Request Payload",
                        JsonUtils.toJson(request)
                ));
            } catch (Exception ignored) {
            }

            /* ✅ API CALL */
            Response response = apiCall.get();

            /* ✅ STATUS CODE */
            test.addStep(new ReportStep(
                    "Info",
                    "Actual Status Code",
                    String.valueOf(response.getStatusCode())
            ));

            /* ✅ CONTENT TYPE CHECK (CRITICAL FIX) */
            String contentType = response.getHeader("Content-Type");

            if (isBinaryResponse(contentType)) {

                test.addStep(new ReportStep(
                        "Info",
                        "Actual Response",
                        "[Binary Response Omitted] Content-Type → " + contentType
                ));

            } else {

                test.addStep(new ReportStep(
                        "Info",
                        "Actual Response",
                        response.asPrettyString()
                ));
            }

            /* ✅ EXPECTED STATUS CODE */
            try {
                int expectedStatusCode =
                        (int) tc.getClass()
                                .getMethod("getExpectedStatusCode")
                                .invoke(tc);

                Assert.assertEquals(
                        response.getStatusCode(),
                        expectedStatusCode,
                        "Status code mismatch"
                );

            } catch (Exception ignored) {
            }

            /* ✅ OPTIONAL STATUS FIELD */
            try {
                String expectedStatus =
                        (String) tc.getClass()
                                .getMethod("getExpectedStatus")
                                .invoke(tc);

                if (expectedStatus != null) {
                    Assert.assertEquals(
                            response.jsonPath().getString("status"),
                            expectedStatus,
                            "Status value mismatch"
                    );
                }
            } catch (Exception ignored) {
            }

            test.markPassed(
                    "Passed in " +
                            (System.currentTimeMillis() - start) + " ms"
            );

        } catch (AssertionError | Exception e) {

            e.printStackTrace();

            test.addStep(new ReportStep(
                    "Fail",
                    "Failure",
                    String.valueOf(e.getMessage())
            ));

            test.markFailed("Test failed");

        } finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }

    /* ======================================================
       GET / DELETE EXECUTOR (Also Binary Safe)
       ====================================================== */
    public static void execute(
            String scenarioName,
            GetMyProjectsTestData.TestCase tc,
            Supplier<Response> apiCall
    ) {

        ReportTest test =
                new ReportTest(scenarioName + " :: " + tc.getName());

        ReportContext.setTest(test);
        long start = System.currentTimeMillis();

        try {

            Response response = apiCall.get();

            String contentType = response.getHeader("Content-Type");

            if (isBinaryResponse(contentType)) {

                test.addStep(new ReportStep(
                        "Info",
                        "Actual Response",
                        "[Binary Response Omitted] Content-Type → " + contentType
                ));

            } else {

                test.addStep(new ReportStep(
                        "Info",
                        "Actual Response",
                        response.asPrettyString()
                ));
            }

            Assert.assertEquals(
                    response.getStatusCode(),
                    tc.getExpectedStatusCode(),
                    "Status code mismatch"
            );

            if (hasMethod(tc, "getMinSize") && tc.getMinSize() != null) {

                List<?> list =
                        response.jsonPath().getList(tc.getListPath());

                Assert.assertNotNull(list, "List is null");
                Assert.assertTrue(
                        list.size() >= tc.getMinSize(),
                        "Expected minimum size: " + tc.getMinSize()
                );
            }

            if (tc.getRequiredField() != null) {
                Object field =
                        response.jsonPath().get(tc.getRequiredField());

                Assert.assertNotNull(
                        field,
                        "Missing field: " + tc.getRequiredField()
                );
            }

            test.markPassed(
                    "Passed in " +
                            (System.currentTimeMillis() - start) + " ms"
            );

        } catch (AssertionError | Exception e) {

            test.addStep(new ReportStep(
                    "Fail",
                    "Failure",
                    String.valueOf(e.getMessage())
            ));

            test.markFailed("Test failed");

        } finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }

    /* ======================================================
       HELPER – Binary Detection (CRITICAL)
       ====================================================== */
    private static boolean isBinaryResponse(String contentType) {

        if (contentType == null) return false;

        contentType = contentType.toLowerCase();

        return contentType.contains("octet-stream")
                || contentType.contains("zip")
                || contentType.contains("pdf")
                || contentType.contains("excel")
                || contentType.contains("spreadsheet");
    }
}
