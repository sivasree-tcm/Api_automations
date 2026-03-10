package tests.system;

import api.system.GetVersionApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class GetVersionTest extends BaseTest {

    public void getVersionApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/system/getVersion.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getVersion.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetVersionApi.getVersion(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Get Version API Failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        String version = response.jsonPath().getString("version");

                        if (version == null || version.isEmpty()) {
                            throw new RuntimeException("❌ version missing in response.");
                        }

                        System.out.println("📦 Application Version → " + version);

                        return response;
                    }
            );
        }
    }
}