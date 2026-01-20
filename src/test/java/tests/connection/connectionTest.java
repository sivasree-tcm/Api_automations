package tests.connection;

import api.connection.ConnectionApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.FailureTracker;
import utils.JsonUtils;

import java.util.List;
import java.util.Map;

public class ConnectionTest extends BaseTest {

    @Test
    public void getConnections() {

        Map<String, Object> testData =
                JsonUtils.readJsonAsMap("testdata/connectionsData/getConnection.json");

        executeTestBlock(testData, "positiveTestCases");
        executeTestBlock(testData, "negativeTestCases");
        executeTestBlock(testData, "boundaryTestCases");
        executeTestBlock(testData, "securityTestCases");
        executeTestBlock(testData, "authorizationTestCases");
    }

    // ================= COMMON EXECUTOR =================

    private void executeTestBlock(Map<String, Object> testData, String blockName) {

        if (!testData.containsKey(blockName)) {
            return;
        }

        List<Map<String, Object>> testCases =
                (List<Map<String, Object>>) testData.get(blockName);

        System.out.println("\n🔹 Executing: " + blockName);

        for (Map<String, Object> testCase : testCases) {

            Response response = ConnectionApi.getConnections(
                    testCase.get("request")
            );

            int expected = (int) testCase.get("expectedStatusCode");
            int actual = response.getStatusCode();

            if (expected == actual) {
                System.out.println("✅ PASSED: " + testCase.get("name"));
            } else {
                System.out.println("⚠ FAILED (Logged): " + testCase.get("name"));

                FailureTracker.addFailure(
                        "❌ [" + blockName + "] "
                                + testCase.get("name")
                                + " | Expected: " + expected
                                + " | Actual: " + actual
                );
            }
        }
    }
}
