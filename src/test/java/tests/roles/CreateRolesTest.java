package tests.roles;

import api.roles.CreateRolesApi;
import base.BaseTest;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TestDataGenerator;

import org.testng.annotations.Test;

import java.util.Map;

public class CreateRolesTest extends BaseTest {


    public void createRoleTest() {

        var testData = JsonUtils.readJson(
                "testdata/rolesData/createRoles.json",
                ConnectionReport.class
        );

        for (var tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // 🔹 Modify request dynamically
                        Map<String, Object> req =
                                (Map<String, Object>) tc.getRequest();

                        req.put("roleName", TestDataGenerator.randomRoleName());
                        req.put("roleDescription", "testing");

                        return CreateRolesApi.createRole(
                                req,
                                tc.getRole(),
                                tc.getAuthType()
                        );
                    }
            );
        }
    }
}
