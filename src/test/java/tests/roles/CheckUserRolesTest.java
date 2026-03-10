package tests.roles;

import api.roles.CheckUserRolesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class CheckUserRolesTest extends BaseTest {

    public void checkUserRoles() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/rolesData/checkUserRoles.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ checkUserRoles.json missing or invalid.");
        }

        String userId = String.valueOf(TokenUtil.getUserId());

        Report.TestCase tc =
                testData.getTestCases().get(0);

        tc.setTcId("CHECK_ROLE_" + userId);
        tc.setName("Check User Role | UserId = " + userId);

        Map<String, Object> request = new HashMap<>();
        request.put("userId", userId);

        // ✅ Include payload in report
        tc.setRequest(request);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            CheckUserRolesApi.checkUserRoles(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch user roles. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 Check User Roles Response → ");
                    System.out.println(response.asPrettyString());

                    return response;
                }
        );
    }
}