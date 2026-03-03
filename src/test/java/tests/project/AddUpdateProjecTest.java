package tests.project;

import api.userManagement.UserManagementApi;
import base.BaseTest;
import tests.user.AddUpdateProjectUserTestData;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.RoleStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.Map;

public class AddUpdateProjecTest extends BaseTest {

    public void addUpdateProjectUserApiTest() {

        AddUpdateProjectUserTestData testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/adduser.json",
                        AddUpdateProjectUserTestData.class
                );

        for (AddUpdateProjectUserTestData.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ Always create a fresh request map (DO NOT mutate original JSON map)
                        Map<String, Object> req = new HashMap<>(tc.getRequest());

                        // ✅ Fetch dynamic values safely
                        Integer projectId = ProjectStore.getProjectId();
                        Integer roleId = RoleStore.getRoleId();
                        Integer actingUserId = TokenUtil.getUserId(tc.getRole());

                        if (projectId == null || roleId == null || actingUserId == null) {
                            throw new RuntimeException("ProjectId / RoleId / ActingUserId is NULL. Check previous flow.");
                        }

                        // ✅ Inject dynamic values
                        req.put("projectId", projectId);
                        req.put("refRoleId", roleId);

                        // ⚠ Replace hardcoded 31 with dynamic value if possible
                        Integer targetUserId = 31; // TODO: Replace with UserStore.getTargetUserId()
                        req.put("targetUserId", targetUserId);

                        // ⚠ If backend derives from token, consider removing this field entirely
                        req.put("userId", actingUserId);

                        // ✅ Debug logging (VERY IMPORTANT)
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