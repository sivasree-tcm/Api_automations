package tests.ats;

import api.ats.LoadATSFilesApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class LoadATSFilesTest extends BaseTest {


    public void loadAtsFilesApiTest() {

        // ✅ Load dummy testcase ONLY for executor reporting
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/ats/loadATSFiles.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        // ✅ Build request dynamically
        Map<String, Object> request = new HashMap<>();
        request.put("userProjectId", "64");
        request.put("userId", TokenUtil.getUserId(tc.getRole()));
        request.put("automationFramework", "C# + Playwright");
        request.put("projectName", "Build-47-test");
        request.put("testCaseNumber", "TC-3530-028");
        request.put("storageType", "S3");
        request.put("requestedType", "MenuPage");

        tc.setRequest(request); // ONLY required field

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {
                    Response response =
                            LoadATSFilesApi.loadATSFiles(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    return response;
                }
        );
    }
}