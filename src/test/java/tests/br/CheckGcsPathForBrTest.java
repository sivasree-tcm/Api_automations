package tests.br;

import api.br.CheckGcsPathForBrApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class CheckGcsPathForBrTest extends BaseTest {

    public void checkGcsPathForBrApiTest() {

        Integer userId = TokenUtil.getUserId();
        Integer projectId = ProjectStore.getProjectId();
        Integer brId = BusinessRequirementStore.getAnyBrId(projectId);

        if (brId == null) {
            throw new RuntimeException("❌ No BR ID found for project.");
        }

        Report testData =
                JsonUtils.readJson(
                        "testdata/br/checkGcsPathForBr.json",
                        Report.class
                );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ checkGcsPathForBr.json missing or invalid.");
        }

        Report.TestCase tc =
                testData.getTestCases().get(0);

        Map<String, Object> request = new HashMap<>();

        request.put("brId", brId);
        request.put("userId", userId);

        // ✅ Payload included in report
        tc.setRequest(request);

        tc.setTcId("CHECK_GCS_PATH_BR_" + brId);
        tc.setName("Check GCS Path For BR - " + brId);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            CheckGcsPathForBrApi.checkGcsPathForBr(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to check GCS path. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 Check GCS Path Response → ");
                    System.out.println(response.asPrettyString());

                    Boolean pathStatus =
                            response.jsonPath().getBoolean("gcStoragePathNotNull");

                    if (pathStatus == null) {
                        throw new RuntimeException("❌ gcStoragePathNotNull missing in response.");
                    }

                    System.out.println("✅ GCS Path Status → " + pathStatus);

                    return response;
                }
        );
    }
}