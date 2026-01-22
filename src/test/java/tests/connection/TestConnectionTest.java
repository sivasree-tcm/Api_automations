package tests.connection;

import api.connection.TestConnectionApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

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
