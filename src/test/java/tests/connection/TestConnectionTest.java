package tests.connection;

import api.connection.TestConnectionApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TestDataGenerator;

import java.util.List;
import java.util.Map;

public class TestConnectionTest extends BaseTest {

    @Test
    public void testConnection() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/connectionsData/testConnection.json",
                        ConnectionReport.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            ConnectionReport testData,
            List<ConnectionReport.TestCase> cases
    ) {

        for (ConnectionReport.TestCase tc : cases) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            Map<String, Object> connection =
                    (Map<String, Object>) request.get("connection");
            // ✅ Dynamic org name
            if (connection.containsKey("orgName")) {
                connection.put(
                        "orgName",
                        TestDataGenerator.generateOrgName()
                );
            }

            // ✅ Dynamic site URL
            if (connection.containsKey("siteUrl")) {
                connection.put(
                        "siteUrl",
                        TestDataGenerator.generateSiteUrl()
                );
            }

            // ✅ Dynamic space name (for GitLab / others)
            if (connection.containsKey("spaceName")) {
                connection.put(
                        "spaceName",
                        TestDataGenerator.generateSpaceName()
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> TestConnectionApi.testConnection(
                            tc.getRequest(),
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
