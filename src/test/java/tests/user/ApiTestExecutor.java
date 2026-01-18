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
       POST / PUT (Request + Response validation)
       ====================================================== */
    public static void execute(
            String scenarioName,
            RegisterUserTestData.TestCase tc,
            Supplier<Response> apiCall
    ) {

        ReportTest test =
                new ReportTest(scenarioName + " :: " + tc.getName());

        ReportContext.setTest(test);
        long start = System.currentTimeMillis();

        try {
            // 🔹 Request
            test.addStep(new ReportStep(
                    "Info",
                    "Request Payload",
                    JsonUtils.toJson(tc.getRequest())
            ));

            // 🔹 API call
            Response response = apiCall.get();

            // 🔹 Response
            test.addStep(new ReportStep(
                    "Info",
                    "Actual Response",
                    response.asPrettyString()
            ));

            // 🔹 Status code
            Assert.assertEquals(
                    response.getStatusCode(),
                    tc.getExpectedStatusCode(),
                    "Status code mismatch"
            );

            // 🔹 Status field (optional)
            if (tc.getExpectedStatus() != null) {
                Assert.assertEquals(
                        response.jsonPath().getString("status"),
                        tc.getExpectedStatus(),
                        "Status value mismatch"
                );
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

            // ❌ DO NOT THROW
        }
        finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }

    /* ======================================================
       GET / DELETE (Validation only – NO request)
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
            // 🔹 API call
            Response response = apiCall.get();

            // 🔹 Response
            test.addStep(new ReportStep(
                    "Info",
                    "Actual Response",
                    response.asPrettyString()
            ));

            // 🔹 Status code
            Assert.assertEquals(
                    response.getStatusCode(),
                    tc.getExpectedStatusCode(),
                    "Status code mismatch"
            );

            // 🔹 Min list size (optional)
            if (tc.getMinSize() != null) {
                List<?> list =
                        response.jsonPath().getList(tc.getListPath());

                Assert.assertNotNull(list, "List is null");
                Assert.assertTrue(
                        list.size() >= tc.getMinSize(),
                        "Expected minimum size: " + tc.getMinSize()
                );
            }

            // 🔹 Required field (optional)
            if (tc.getRequiredField() != null) {
                Object field =
                        response.jsonPath().get(tc.getRequiredField());

                Assert.assertNotNull(
                        field,
                        "Missing field: " + tc.getRequiredField()
                );
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
            throw e;

        } finally {
            CustomReportManager.addTest(scenarioName, test);
        }
    }
}
