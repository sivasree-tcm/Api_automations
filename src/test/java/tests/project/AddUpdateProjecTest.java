package tests.project;

import api.UserManagement.UserManagementApi;
import base.BaseTest;
import tests.user.AddUpdateProjectUserTestData;
import tests.user.ApiTestExecutor;
import utils.*;

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

                        Map<String, Object> req = new HashMap<>();

                        // ✅ DYNAMICALLY FETCHED VALUES
                        req.put("projectId", ProjectStore.getProjectId());
                        req.put("refRoleId", RoleStore.getRoleId());
                        req.put("targetUserId", 32);
                        Integer userId = TokenUtil.getUserId(tc.getRole());

                        req.put("userId", String.valueOf(userId)); // match Postman exactly
                        req.put("isAdmin", tc.getAdmin());

                        return UserManagementApi.addOrUpdateProjectUser(
                                req,
                                tc.getRole()
                        );
                    }
            );
        }
    }
}