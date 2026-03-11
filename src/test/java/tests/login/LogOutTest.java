package tests.login;

import api.login.LogOutApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.Map;
import java.util.List;

public class LogOutTest extends BaseTest {

    public void logoutTest() {
        Report testData = JsonUtils.readJson(
                "testdata/authData/logOut.json",
                Report.class
        );

        execute(testData, testData.getTestCases());
    }

    private void execute(Report testData, List<Report.TestCase> cases) {
        for (Report.TestCase tc : cases) {

            @SuppressWarnings("unchecked")
            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            // 🛠 Dynamic userId Injection
            if (request != null && request.containsKey("userId")) {
                Object userIdVal = request.get("userId");

                if (userIdVal != null && userIdVal.toString().contains("{{userId}}")) {

                    // Determine which user's ID to fetch based on role
                    boolean isSpecialRole = tc.getRole() != null &&
                            (tc.getRole().equalsIgnoreCase("NO_AUTH") ||
                                    tc.getRole().equalsIgnoreCase("INVALID_TOKEN"));

                    int dynamicUserId = isSpecialRole
                            ? TokenUtil.getUserId("SUPER_ADMIN")
                            : TokenUtil.getUserId(tc.getRole());

                    request.put("userId", String.valueOf(dynamicUserId));
                }
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        Response res = LogOutApi.logout(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        res.prettyPrint(); // Debugging purposes
                        return res;
                    }
            );
        }
    }
}