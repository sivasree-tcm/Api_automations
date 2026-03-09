package tests.connection;

import api.connection.GetConnectionsApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetConnectionsTest extends BaseTest {

    public void getConnectionsApiTest() {

        Integer userId = TokenUtil.getUserId();
        Integer orgId = OrganizationStore.getOrgId();

        if (userId == null) {
            throw new RuntimeException("❌ userId is null.");
        }

        if (orgId == null) {
            throw new RuntimeException("❌ orgId is null.");
        }

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/connectionsData/getConnections.json",
                        ConnectionReport.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getConnections.json missing or invalid.");
        }

        // ✅ Use existing testcase instead of creating new object
        ConnectionReport.TestCase tc =
                testData.getTestCases().get(0);

        Map<String, Object> request = new HashMap<>();

        request.put("userId", userId);
        request.put("orgId", orgId);

        // ✅ Payload will appear in report
        tc.setRequest(request);

        tc.setTcId("GET_CONNECTIONS_" + orgId);
        tc.setName("Get Connections - Org " + orgId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetConnectionsApi.getConnections(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ GetConnections API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch connections. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 Get Connections Response → ");
                    System.out.println(response.asPrettyString());

                    Boolean success =
                            response.jsonPath().getBoolean("success");

                    if (success == null || !success) {
                        throw new RuntimeException("❌ success flag is false.");
                    }

                    List<Map<String, Object>> connections =
                            response.jsonPath().getList("data");

                    if (connections == null || connections.isEmpty()) {
                        throw new RuntimeException("❌ No connections found.");
                    }

                    System.out.println("✅ Total Connections Found → " + connections.size());

                    return response;
                }
        );
    }
}