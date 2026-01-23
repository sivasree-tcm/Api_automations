//package tests.connection;
//
//import api.connection.GetConnectionApi;
//import api.connection.SaveConnectionApi;
//import base.BaseTest;
//import org.testng.annotations.Test;
//import tests.user.ApiTestExecutor;
//import utils.JsonUtils;
//
//import java.util.List;
//
//public class SaveConnectionTest extends BaseTest {
//
//    @Test
//    public void saveConnectionTest() {
//
//        ConnectionReport testData =
//                JsonUtils.readJson(
//                        "testdata/connectionsData/saveConnection.json",
//                        ConnectionReport.class
//                );
//
//        execute(testData, testData.getTestCases());
//    }
//
//    private void execute(
//            ConnectionReport testData,
//            List<ConnectionReport.TestCase> cases
//    ) {
//
//        for (ConnectionReport.TestCase tc : cases) {
//
//            // ✅ STEP 1: Fetch existing connections
//            GetConnectionApi.getConnections(
//                    tc.getRequest(),
//                    tc.getRole(),
//                    "VALID"
//            );
//
//            // ✅ STEP 2: Extracted IDs are already stored internally
//            List<Integer> existingIds =
//                    GetConnectionApi.getExtractedConnectionIds();
//
//            if (!existingIds.isEmpty()) {
//                System.out.println(
//                        "⚠ Existing connections found: " + existingIds
//                );
//
//                // 🔜 Future implementation
//                // for (Integer id : existingIds) {
//                //     DeleteConnectionApi.delete(id);
//                // }
//            }
//
//            // ✅ STEP 3: Save connection
//            ApiTestExecutor.execute(
//                    testData.getScenario(),
//                    tc,
//                    () -> SaveConnectionApi.saveConnection(
//                            tc.getRequest(),
//                            tc.getRole(),
//                            tc.getAuthType()
//                    )
//            );
//        }
//    }
//}
