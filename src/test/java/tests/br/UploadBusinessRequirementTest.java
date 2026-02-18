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

    @Test
    public void uploadBusinessRequirementTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/br/uploadBusinessRequirement.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request = (Map<String, Object>) tc.getRequest();

            int userId = TokenUtil.getUserId(tc.getRole());
            Integer projectId = ProjectStore.getProjectId();

            if (projectId == null) {
                throw new RuntimeException("❌ Project ID not available");
            }

            File brFile = new File("src/test/resources/files/BR_Sample (6).xlsx");

            if (!brFile.exists()) {
                throw new AssertionError("❌ BR file not found at: " + brFile.getAbsolutePath());
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        Response response = UploadBusinessRequirementApi.uploadBR(
                                brFile,
                                userId,
                                projectId,
                                TokenUtil.getToken(tc.getRole())
                        );

                        // ✅ MODIFIED LOGIC
                        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {

                            System.out.println("✅ Upload successful message received. Attempting to retrieve IDs...");

                            // 1. Try to get IDs from response first (if they exist)
                            List<Integer> brIds = response.jsonPath().getList("br_ids");

                            if (brIds != null && !brIds.isEmpty()) {
                                BusinessRequirementStore.store(projectId, brIds);
                                System.out.println("✅ Stored IDs from Response: " + brIds);
                            } else {
                                // 2. Fallback: If response has no IDs, trigger the Fetch API to sync the store
                                System.out.println("⚠️ No IDs in response. Syncing with GetBusinessRequirement API...");

                                // This calls your Step 17 logic inside Step 16 to ensure data is available for Step 18
                                new tests.br.GetBusinessRequirementTest().fetchBRsForProject();

                                // Log the result of the sync
                                if (BusinessRequirementStore.getIds(projectId).isEmpty()) {
                                    System.err.println("⚠️ Warning: Sync completed but Store is still empty. " +
                                            "Database might be processing the file asynchronously.");
                                }
                            }
                        }
                        return response;
                    }
            );
        }
    }
}