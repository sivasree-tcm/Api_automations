package tests.user;

import api.UserManagement.UserManagementApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.user.AddUpdateProjectUserTestData;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

import java.util.List;

public class AddUpdateProjectUserTest extends BaseTest {

    @Test
    public void addUpdateProjectUserApiTest() {

        AddUpdateProjectUserTestData testData =
                JsonUtils.readJson(
                        "testdata/UserManagement/addUpdateProjectUser.json",
                        AddUpdateProjectUserTestData.class
                );

        execute(testData, testData.getTestCases());
    }

    private void execute(
            AddUpdateProjectUserTestData testData,
            List<AddUpdateProjectUserTestData.TestCase> cases
    ) {

        for (AddUpdateProjectUserTestData.TestCase tc : cases) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> UserManagementApi.addOrUpdateProjectUser(
                            tc.getRequest(),
                            tc.getRole()
                    )
            );
        }
    }
}
