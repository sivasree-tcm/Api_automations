//package tests.roles;
//
//import api.roles.CheckUserRolesApi;
//import base.BaseTest;
//import io.restassured.response.Response;
//import tests.connection.ConnectionReport;
//import tests.user.ApiTestExecutor;
//import utils.JsonUtils;
//import utils.ProjectUserStore;
//import utils.TokenUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class CheckUserRolesTest extends BaseTest {
//
//    public void checkUserRoles() {
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/roles/checkUserRoles.json",
//                        ConnectionReport.class
//                );
//
//        // Loop through all users collected earlier
//        ProjectUserStore.getAllUsers().forEach(user -> {
//
//            Integer userId = (Integer) user.get("userId");
//
//            ConnectionReport.TestCase tc =
//                    new ConnectionReport.TestCase(
//                            testData.getTestCases().get(0)
//                    );
//
//            tc.setTcId("CHECK_ROLE_" + userId);
//            tc.setName("Check User Role | UserId = " + userId);
//
//            Map<String, Object> request = new HashMap<>();
//            request.put("userId", userId);
//
//            tc.setRequest(request);
//
//            ApiTestExecutor.execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> {
//
//                        Response response =
//                                CheckUserRolesApi.checkUserRoles(
//                                        request,
//                                        tc.getRole(),
//                                        tc.getAuthType()
//                                );
//
//                        return response;
//                    }
//            );
//        });
//    }
//}
