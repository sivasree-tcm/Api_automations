package tests.project;

import api.UserManagement.UserManagementApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
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

                        req.put("targetUserId", 35);
                        req.put("projectId", ProjectStore.getProjectId());
                        req.put("refRoleId", RoleStore.getRoleId());
                        req.put("userId", TokenUtil.getUserId(tc.getRole()));

                        // ✅ CORRECT FIELD
                        req.put("isAdmin", tc.getAdmin());
//                        req.put("targetUserId", 32);
//                        req.put("projectId", 1611);
//                        req.put("refRoleId", 1261);
//                        req.put("userId", TokenUtil.getUserId(tc.getRole()));
//
//                        // ✅ CORRECT FIELD
//                        req.put("isAdmin", tc.getAdmin());

                        return UserManagementApi.addOrUpdateProjectUser(
                                req,
                                tc.getRole()
                        );
                    }
            );
        }
    }
}
