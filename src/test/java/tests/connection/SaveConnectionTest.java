package tests.connection;
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

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/connectionsData/saveConnection.json",
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

                Object siteUrl = connection.get("siteUrl");

                // Generate ONLY when test data explicitly says AUTO
                if ("__AUTO__".equals(siteUrl)) {
                    connection.put(
                            "siteUrl",
                            TestDataGenerator.generateSiteUrl()
                    );
                }
                // else → keep whatever test data provided (valid or invalid)
            }

            if (connection.containsKey("endDate")) {

                Object endDate = connection.get("endDate");

                // Auto-generate ONLY when explicitly asked
                if ("__AUTO__".equals(endDate)) {
                    connection.put(
                            "endDate",
                            TestDataGenerator.generateFutureDate()
                    );
                }
                // else → keep JSON value as-is (valid / invalid / past)
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
                    () -> SaveConnectionApi.saveConnection(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }


}
