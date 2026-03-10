package tests.user;

import api.userManagement.UserManagementApi;
import base.BaseTest;
import report.Report;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class AddUpdateProjecTest extends BaseTest {

    public void addUpdateProjectUserApiTest() {

        Report testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/adduser.json",
                        Report.class
                );

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> req;

                        if (tc.getRequest() != null) {
                            req = new HashMap<>((Map<String, Object>) tc.getRequest());
                        } else {
                            req = new HashMap<>();
                        }

                        // ✅ Fetch dynamic values
                        Integer projectId = ProjectStore.getProjectId();
                        Integer roleId = RoleStore.getRoleId();
                        Integer actingUserId = TokenUtil.getUserId(tc.getRole());

                        if (projectId == null || roleId == null || actingUserId == null) {
                            throw new RuntimeException(
                                    "ProjectId / RoleId / ActingUserId is NULL. Check previous flow."
                            );
                        }

                        // ✅ Inject dynamic values
                        req.put("projectId", projectId);
                        req.put("refRoleId", roleId);

                        // ⚠ Replace with dynamic user store if available
                        Integer targetUserId = 31;
                        req.put("targetUserId", targetUserId);

                        req.put("userId", actingUserId);

                        // ✅ Save payload to report
                        tc.setRequest(req);

                        // ✅ Debug logs
                        System.out.println("--------------------------------------------------");
                        System.out.println("SCENARIO      : " + testData.getScenario());
                        System.out.println("TC ID         : " + tc.getTcId());
                        System.out.println("ROLE          : " + tc.getRole());
                        System.out.println("PROJECT ID    : " + projectId);
                        System.out.println("ROLE ID       : " + roleId);
                        System.out.println("ACTING USER   : " + actingUserId);
                        System.out.println("TARGET USER   : " + targetUserId);
                        System.out.println("FINAL REQUEST : " + req);
                        System.out.println("--------------------------------------------------");

                        return UserManagementApi.addOrUpdateProjectUser(
                                req,
                                tc.getRole()
                        );
                    }
            );
        }
    }
}