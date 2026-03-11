package tests.configuration;

import api.configuration.GetConfigApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class GetConfigTest extends BaseTest {

    public void getConfigApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/configuration/getConfig.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getConfig.json missing or invalid.");
        }

        Report.TestCase tc = testData.getTestCases().get(0);

        Map<String,Object> request = new HashMap<>();

        request.put("userId", TokenUtil.getUserId());
        request.put("projectId", ProjectStore.getSelectedProjectId());

        // Include payload in report
        tc.setRequest(request);

        tc.setTcId("GET_CONFIG_" + ProjectStore.getSelectedProjectId());
        tc.setName("Get Project Configuration");

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetConfigApi.getConfig(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if(response.getStatusCode()==200){

                        Boolean success =
                                response.jsonPath().getBoolean("success");

                        if(success == null || !success){
                            throw new RuntimeException("❌ API success flag false");
                        }

                        System.out.println("✅ Config fetched successfully");
                    }

                    return response;
                }
        );
    }
}