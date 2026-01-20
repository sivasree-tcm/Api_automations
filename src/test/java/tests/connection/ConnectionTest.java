package tests.connection;

import api.connection.ConnectionApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class ConnectionTest extends BaseTest {

    @Test
    public void getConnectionsTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/connectionsData/getConnection.json",
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
                    () -> ConnectionApi.getConnections(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}