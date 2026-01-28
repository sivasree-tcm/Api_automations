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

    @Test
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
                    () -> SaveConnectionApi.saveConnection(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }


}
