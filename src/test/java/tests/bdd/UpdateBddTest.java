package tests.bdd;

import api.bdd.UpdateBddApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class UpdateBddTest extends BaseTest {
@Test
    public void updateBdd() {

        Integer userId = TokenUtil.getUserId();

        if (userId == null) {
            throw new RuntimeException("❌ UserId missing from TokenUtil.");
        }

        // ✅ Reusing common testdata model
        ConnectionReport testData = JsonUtils.readJson(
                "testdata/bdd/updateBdd.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ updateBdd.json missing or invalid.");
        }

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> request =
                                (tc.getRequest() != null)
                                        ? new HashMap<>((Map<String, Object>) tc.getRequest())
                                        : new HashMap<>();

                        /* ✅ Inject dynamic userId */
                        request.put("userId", userId.toString());

                        tc.setRequest(request);

                        System.out.println("📦 Update BDD Payload → " + request);

                        Response response = UpdateBddApi.updateBdd(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Update BDD Failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        System.out.println("✅ Update BDD executed successfully");

                        return response;
                    }
            );
        }
    }
}