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

    /* ======================================================
       POST / PUT (GENERIC – Register, Project, etc.)
       ====================================================== */
    private static boolean hasMethod(Object obj, String methodName) {
        try {
            obj.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

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
            // 🔹 Log Request if present
            try {
                Object request =
                        tc.getClass().getMethod("getRequest").invoke(tc);

                test.addStep(new ReportStep(
                        "Info",
                        "Request Payload",
                        JsonUtils.toJson(request)
                ));
            } catch (Exception ignored) {
                // Not all test cases have request
            }

            // 🔹 API call
            Response response = apiCall.get();

            // 🔹 Actual Status Code
            test.addStep(new ReportStep(
                    "Info",
                    "Actual Status Code",
                    String.valueOf(response.getStatusCode())
            ));

            // 🔹 Actual Response
            test.addStep(new ReportStep(
                    "Info",
                    "Actual Response",
                    response.asPrettyString()
            ));

            // 🔹 Expected Status Code (if exists)
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
                // Some test cases may not have expectedStatusCode
            }

            // 🔹 Expected Status field (optional)
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

            // ✅ PASS
            test.markPassed(
                    "Passed in " +
                            (System.currentTimeMillis() - start) + " ms"
            );

        } catch (AssertionError | Exception e) {

            test.addStep(new ReportStep(
                    "Fail",
                    "Failure",
                    e.getMessage()
            ));

            test.markFailed("Test failed");


        } finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }

    /* ======================================================
       GET / DELETE (SPECIAL CASE – List validation)
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

            test.addStep(new ReportStep(
                    "Info",
                    "Actual Response",
                    response.asPrettyString()
            ));

            Assert.assertEquals(
                    response.getStatusCode(),
                    tc.getExpectedStatusCode(),
                    "Status code mismatch"
            );

            // ===== OPTIONAL LIST VALIDATION =====
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
                    e.getMessage()
            ));
            test.markFailed("Test failed");
            throw e;

        } finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }
}
