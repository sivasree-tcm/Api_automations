package tests.connection;

import api.connection.DeleteConnectionApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.ConnectionStore;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class DeleteConnectionTest extends BaseTest {

    public void deleteConnectionTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/connectionsData/deleteConnection.json",
                        Report.class
                );

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        /* Get stored connectionId from Step1 */
                        Integer connectionId = ConnectionStore.getConnectId();

                        if (connectionId == null) {
                            throw new RuntimeException("❌ No connectionId found in store");
                        }

                        Map<?, ?> request = (Map<?, ?>) tc.getRequest();

                        /* Dynamic UserId */
                        Integer userId = TokenUtil.getUserId();

                        /* OrgId from JSON */
                        Object orgObj = request.get("orgId");

                        if (orgObj == null) {
                            throw new RuntimeException("❌ orgId missing in deleteConnection.json");
                        }

                        Integer orgId = Integer.valueOf(orgObj.toString());

                        /* Build delete payload */
                        Map<String, Object> deleteRequest = new HashMap<>();
                        deleteRequest.put("userId", userId);
                        deleteRequest.put("orgId", orgId);
                        deleteRequest.put("connectionId", connectionId);

                        System.out.println("Deleting Connection → " + deleteRequest);

                        /* Call API */
                        Response res = DeleteConnectionApi.deleteConnection(
                                deleteRequest,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        return res;
                    }
            );
        }
    }
}