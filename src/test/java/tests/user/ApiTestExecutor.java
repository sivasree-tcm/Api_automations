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
            // ✅ SAFE fallback (prevents null null)
            String method =
                    tc.getMethod() != null ? tc.getMethod() : "POST";

            String endpoint =
                    tc.getEndpoint() != null ? tc.getEndpoint() : "/api/user/register";

            // 🔹 Endpoint
            test.addStep(new ReportStep(
                    "Info",
                    "Endpoint",
                    method + " " + endpoint
            ));

            // 🔹 Expected Status
            test.addStep(new ReportStep(
                    "Info",
                    "Expected Status",
                    "HTTP " + tc.getExpectedStatusCode() +
                            (tc.getExpectedStatus() != null
                                    ? ", status = " + tc.getExpectedStatus()
                                    : "")
            ));

            // 🔹 Request Payload
            test.addStep(new ReportStep(
                    "Info",
                    "Request Payload",
                    JsonUtils.toJson(tc.getRequest())
            ));

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

            // 🔹 Status code assertion
            Assert.assertEquals(
                    response.getStatusCode(),
                    tc.getExpectedStatusCode(),
                    "Status code mismatch"
            );

            // 🔹 Status field assertion (optional)
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

        } finally {
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

            // 🔹 Actual Response
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
