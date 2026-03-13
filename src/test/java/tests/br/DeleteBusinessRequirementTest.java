package tests.br;

import api.br.DeleteBRApi;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.*;

public class DeleteBusinessRequirementTest {

    public void deleteLastBR() {

        // 1. Load template data
        Report testData = JsonUtils.readJson(
                "testdata/br/deleteBR.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ deleteBR.json missing or invalid");
        }

        // 2. Iterate through projects
        BusinessRequirementStore.getAll().forEach((projectId, brIds) -> {

            if (brIds == null || brIds.isEmpty()) {
                System.out.println("ℹ No BRs found to delete for project: " + projectId);
                return;
            }

            // 3. Get last BR ID
            Integer lastBrId = brIds.get(brIds.size() - 1);

            // 4. Create reporting test case
            Report.TestCase tc = new Report.TestCase(
                    testData.getTestCases().get(0)
            );

            // 5. Build request payload (NEW FORMAT)
            Map<String, Object> request = new HashMap<>();

            request.put("brId", Collections.singletonList(lastBrId)); // API now expects LIST
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("DEL_LAST_BR_" + lastBrId);
            tc.setName("Delete Last BR | ID: " + lastBrId + " | Project: " + projectId);
            tc.setRequest(request);

            System.out.println("REQUEST → " + request);

            // 6. Execute API
            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response = DeleteBRApi.deleteBR(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        System.out.println("STATUS → " + response.getStatusCode());
                        System.out.println("BODY → " + response.asString());

                        // 7. If successful update Store
                        if (response.getStatusCode() == 200 ||
                                response.getStatusCode() == 204) {

                            BusinessRequirementStore.removeBrId(projectId, lastBrId);

                            System.out.println(
                                    "🗑 Successfully deleted last BR: "
                                            + lastBrId +
                                            " from Project: " +
                                            projectId
                            );

                        } else {

                            System.err.println(
                                    "❌ Failed to delete BR: "
                                            + lastBrId +
                                            " Status: "
                                            + response.getStatusCode()
                            );
                        }

                        return response;
                    }
            );
        });
    }
}