package tests.connection;

import io.restassured.response.Response;
import report.Report;
import utils.ConnectionStore;
import utils.TestDataGenerator;
import api.connection.SaveConnectionApi;
import base.BaseTest;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.Map;
import java.util.List;

public class SaveConnectionTest extends BaseTest {

    public void saveConnectionTest() {

        Report testData = JsonUtils.readJson(
                "testdata/connectionsData/saveConnection.json",
                Report.class
        );

        execute(testData, testData.getTestCases());
    }

    private void execute(Report testData, List<Report.TestCase> cases) {

        for (Report.TestCase tc : cases) {

            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // Dynamic userId
            if (request.containsKey("userId")) {
                Object userIdVal = request.get("userId");

                if (userIdVal != null && userIdVal.toString().contains("{{userId}}")) {

                    boolean isSpecialRole = tc.getRole() != null &&
                            (tc.getRole().equalsIgnoreCase("NO_AUTH") ||
                                    tc.getRole().equalsIgnoreCase("INVALID_TOKEN"));

                    int dynamicUserId = isSpecialRole
                            ? TokenUtil.getUserId("SUPER_ADMIN")
                            : TokenUtil.getUserId(tc.getRole());

                    request.put("userId", dynamicUserId);
                }
            }

            Map<String, Object> connection = (Map<String, Object>) request.get("connection");
            if (connection == null) continue;

            // Dynamic org name
            if (connection.containsKey("orgName")) {
                connection.put("orgName", TestDataGenerator.generateOrgName());
            }

            // Dynamic site URL
            if (connection.containsKey("siteUrl")) {
                Object siteUrl = connection.get("siteUrl");

                if ("__AUTO__".equals(siteUrl)) {
                    connection.put("siteUrl", TestDataGenerator.generateSiteUrl());
                }
            }

            // Dynamic end date
            if (connection.containsKey("endDate")) {
                Object endDate = connection.get("endDate");

                if ("__AUTO__".equals(endDate)) {
                    connection.put("endDate", TestDataGenerator.generateFutureDate());
                }
            }

            // Dynamic space name
            if (connection.containsKey("spaceName")) {
                connection.put("spaceName", TestDataGenerator.generateSpaceName());
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response res = SaveConnectionApi.saveConnection(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        res.prettyPrint();   // Debug response

                        if (res.getStatusCode() == 200 || res.getStatusCode() == 201) {

                            try {

                                int id = res.jsonPath().getInt("data.id");

                                ConnectionStore.setConnectId(id);

                                System.out.println("Stored Connection ID: " + id);

                            } catch (Exception e) {

                                System.err.println("Could not parse connection ID: " + e.getMessage());
                            }
                        }

                        return res;
                    }
            );
        }
    }
}