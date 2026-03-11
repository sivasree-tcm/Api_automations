package tests.userManagement;

import api.userManagement.ToggleUserStatusApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class ToggleUserStatusTest extends BaseTest {


    public void toggleUserStatusApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/userManagement/toggleUserStatus.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ ToggleUserStatusTest test data missing");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> req = new HashMap<>();

                        /* -------- JSON REQUEST VALUES -------- */

                        Map<?, ?> request =
                                (Map<?, ?>) tc.getRequest();

                        Integer targetUserId =
                                Integer.valueOf(request.get("targetUserId").toString());

                        Integer isActive =
                                Integer.valueOf(request.get("isActive").toString());

                        req.put("targetUserId", targetUserId);
                        req.put("isActive", isActive);

                        /* -------- DYNAMIC VALUES -------- */

                        req.put("projectId", ProjectStore.getProjectId());
                        req.put("refRoleId", RoleStore.getRoleId());
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));

                        System.out.println("📦 ToggleUserStatusTest Payload → " + req);

                        Response response =
                                ToggleUserStatusApi.toggleUserStatus(
                                        req,
                                        tc.getRole()
                                );

                        return response;
                    }
            );
        }
    }
}