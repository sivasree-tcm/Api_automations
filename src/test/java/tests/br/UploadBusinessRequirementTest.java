package tests.br;

import api.br.UploadBusinessRequirementApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadBusinessRequirementTest extends BaseTest {


    public void uploadBusinessRequirementTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/br/uploadBusinessRequirement.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            // 🔁 Dynamic userId
            int userId = TokenUtil.getUserId(tc.getRole());
            request.put("userId", userId);

            // 🔁 Dynamic projectId
            Integer projectId = ProjectStore.getProjectId();
            if (projectId == null) {
                throw new RuntimeException("❌ Project ID not available");
            }
            request.put("projectId", projectId);

            // 📂 File path (as requested)
            File brFile = new File(
                    "src/test/resources/files/BR_Sample (6).xlsx"
            );

            if (!brFile.exists()) {
                throw new AssertionError(
                        "❌ BR file not found at path: " + brFile.getAbsolutePath()
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                UploadBusinessRequirementApi.uploadBR(
                                        brFile,
                                        userId,
                                        projectId,
                                        TokenUtil.getToken(tc.getRole())
                                );

                        // ✅ STORE BR IDs AFTER UPLOAD
                        if (response.getStatusCode() == 200) {

                            List<Integer> brIds = new ArrayList<>();

                            // Case 1: { "brIds": [...] }
                            List<Integer> directIds =
                                    response.jsonPath().getList("brIds");

                            if (directIds != null && !directIds.isEmpty()) {
                                brIds.addAll(directIds);
                            }

                            // Case 2: { "results": [ { "brId": X } ] }
                            List<Map<String, Object>> results =
                                    response.jsonPath().getList("results");

                            if (results != null) {
                                for (Map<String, Object> item : results) {
                                    Object id = item.get("brId");
                                    if (id instanceof Number) {
                                        brIds.add(((Number) id).intValue());
                                    }
                                }
                            }

                            if (brIds.isEmpty()) {
                                throw new RuntimeException(
                                        "❌ Upload succeeded but no BR IDs returned"
                                );
                            }

                            // 🔥 Store BR IDs for TS generation
                            BusinessRequirementStore.store(
                                    projectId,
                                    brIds
                            );

                            System.out.println(
                                    "✅ Stored BR IDs for project "
                                            + projectId + " → " + brIds
                            );
                        }

                        return response;
                    }
            );
        }
    }
}
