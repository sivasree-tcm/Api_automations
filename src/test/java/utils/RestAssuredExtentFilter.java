package utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RestAssuredExtentFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification response,
                           FilterContext ctx) {

        // 🔹 Skip logging for LOGIN API
        if (request.getURI().contains("/api/login")) {
            return ctx.next(request, response);
        }

        // Execute API call
        Response res = ctx.next(request, response);

        // 🔹 AUTO-CAPTURED (NO hardcoding)
        ExtentTestListener.getTest().info(
                "HTTP Method: " + request.getMethod()
        );

        ExtentTestListener.getTest().info(
                "Endpoint: " + request.getURI()
        );

        ExtentTestListener.getTest().info(
                "<b>Request Headers:</b><pre>" +
                        request.getHeaders() +
                        "</pre>"
        );

        if (request.getBody() != null) {
            ExtentTestListener.getTest().info(
                    "<b>Request Body:</b><pre>" +
                            request.getBody() +
                            "</pre>"
            );
        }

        ExtentTestListener.getTest().info(
                "<b>Status Code:</b> " + res.getStatusCode()
        );

        ExtentTestListener.getTest().info(
                "<b>Response:</b><pre>" +
                        res.asPrettyString() +
                        "</pre>"
        );

        return res;
    }
}
