//package tests.connection;
//
//import base.BaseTest;
//import org.testng.annotations.Test;
//import tests.user.ApiTestExecutor;
//import utils.DeleteConnectionUtil;
//import utils.JsonUtils;
//
//public class DeleteConnectionTest extends BaseTest {
//
//    @Test
//    public void deleteConnectionTest() {
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/connectionsData/deleteConnection.json",
//                        ConnectionReport.class
//                );
//
//        for (ConnectionReport.TestCase tc : testData.getTestCases()) {
//
//            ApiTestExecutor.<ConnectionReport.TestCase>execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> DeleteConnectionUtil.deleteAllConnections(tc)
//            );
//        }
//    }
//}
