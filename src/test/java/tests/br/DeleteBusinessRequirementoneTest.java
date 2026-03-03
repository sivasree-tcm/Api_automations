package tests.br;

import api.br.DeleteBRApi;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;
import java.util.*;

public class DeleteBusinessRequirementoneTest {

    public void deleteLastBR() {
        // 1. Load the template data from JSON
        ConnectionReport testData = JsonUtils.readJson(
                "testdata/br/deleteBR.json",
                ConnectionReport.class
        );

        // 2. Iterate through all projects in the Store
        BusinessRequirementStore.getAll().forEach((projectId, brIds) -> {

            if (brIds == null || brIds.isEmpty()) {
                System.out.println("ℹ No BRs found to delete for project: " + projectId);
                return;
            }

            // 3. Identify the LAST ID in the list
            Integer lastBrId = brIds.get(brIds.size() - 1);

            // 4. Set up the Test Case reporting object
            ConnectionReport.TestCase tc = new ConnectionReport.TestCase(
                    testData.getTestCases().get(0)
            );

            Map<String, Object> request = new HashMap<>();
            request.put("brId", lastBrId); // Passing single ID, not the list
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("DEL_LAST_BR_" + lastBrId);
            tc.setName("Delete Last BR | ID: " + lastBrId + " | Project: " + projectId);
            tc.setRequest(request);

            // 5. Execute the API Call
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {
                        Response response = DeleteBRApi.deleteBR(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        // 6. If successful (200 OK or 204 No Content), update the Store
                        if (response.getStatusCode() == 200 || response.getStatusCode() == 204) {
                            BusinessRequirementStore.removeBrId(projectId, lastBrId);
                            System.out.println("🗑 Successfully deleted last BR: " + lastBrId + " from Project: " + projectId);
                        } else {
                            System.err.println("❌ Failed to delete BR: " + lastBrId + " Status: " + response.getStatusCode());
                        }

                        return response;
                    }
            );
        });
    }
}