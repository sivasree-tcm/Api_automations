package tests.connection;

import io.restassured.response.Response;
import utils.ConnectionStore;
import utils.TestDataGenerator;
import api.connection.SaveConnectionApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import java.util.Map;
import java.util.List;

public class SaveConnectionTest extends BaseTest {


    public void saveConnectionTest() {
        ConnectionReport testData = JsonUtils.readJson(
                "testdata/connectionsData/saveConnection.json",
                ConnectionReport.class
        );

        execute(testData, testData.getTestCases());
    }

    private void execute(ConnectionReport testData, List<ConnectionReport.TestCase> cases) {

        for (ConnectionReport.TestCase tc : cases) {

            // 1. Get the main request map
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // 2. Get the nested connection object (Declared ONCE here)
            Map<String, Object> connection = (Map<String, Object>) request.get("connection");

            if (connection == null) continue;

            // ✅ Dynamic org name
            if (connection.containsKey("orgName")) {
                connection.put("orgName", TestDataGenerator.generateOrgName());
            }

            // ✅ Dynamic site URL
            if (connection.containsKey("siteUrl")) {
                Object siteUrl = connection.get("siteUrl");
                if ("__AUTO__".equals(siteUrl)) {
                    connection.put("siteUrl", TestDataGenerator.generateSiteUrl());
                }
            }

            // ✅ Dynamic End Date
            if (connection.containsKey("endDate")) {
                Object endDate = connection.get("endDate");
                if ("__AUTO__".equals(endDate)) {
                    connection.put("endDate", TestDataGenerator.generateFutureDate());
                }
            }

            // ✅ Dynamic space name
            if (connection.containsKey("spaceName")) {
                connection.put("spaceName", TestDataGenerator.generateSpaceName());
            }

            // 3. Capture the platform type before executing (e.g., "azure", "jira")
            String currentPlatform = (String) connection.get("type");

            // 4. Execute API call and capture response data
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        Response res = SaveConnectionApi.saveConnection(request, tc.getRole(), tc.getAuthType());

                        // ✅ Extract ID and Platform only on success (200 or 201)
                        if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {
                            try {
                                int id = res.jsonPath().getInt("data.id");
                                ConnectionStore.setData(id, currentPlatform);
                                System.out.println("DEBUG: Stored Connection ID [" + id + "] for platform [" + currentPlatform + "]");
                            } catch (Exception e) {
                                System.err.println("ERROR: Failed to parse connection ID from response: " + e.getMessage());
                            }
                        }
                        return res;
                    }
            );
        }
    }
}