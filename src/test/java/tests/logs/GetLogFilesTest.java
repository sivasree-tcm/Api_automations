package tests.logs;

import api.logs.GetLogFilesApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test
public class GetLogFilesTest extends BaseTest {

    public void getLogFilesApiTest() {

        Integer userId = TokenUtil.getUserId();

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/logs/getLogFiles.json",
                ConnectionReport.class
        );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);

            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetLogFilesApi.getLogFiles(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException("❌ Get Log Files API Failed");
                        }

                        List<Map<String, Object>> logs =
                                response.jsonPath().getList("");

                        for (Map<String, Object> log : logs) {
                            String name = (String) log.get("name");
                            LogStore.addLogFile(name);
                            System.out.println("📄 Log Stored → " + name);
                        }

                        return response;
                    }
            );
        }
    }
}