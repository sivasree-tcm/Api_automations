package tests.br;

import api.br.DeleteBRApi;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.*;

public class DeleteBusinessRequirementTest {

    public void deleteBRs() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/br/deleteBR.json",
                        ConnectionReport.class
                );

        BusinessRequirementStore.getAll().forEach((projectId, brIds) -> {

            if (brIds == null || brIds.isEmpty()) {
                System.out.println("ℹ No BRs to delete for project " + projectId);
                return;
            }

            ConnectionReport.TestCase tc =
                    new ConnectionReport.TestCase(
                            testData.getTestCases().get(0)
                    );

            Map<String, Object> request = new HashMap<>();
            request.put("brId", brIds);
            request.put("userId", TokenUtil.getUserId());

            tc.setTcId("DEL_BR_" + projectId);
            tc.setName("Delete BR | Project " + projectId);
            tc.setRequest(request);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                DeleteBRApi.deleteBR(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        System.out.println(
                                "🗑 BR deleted → project=" + projectId +
                                        " | brIds=" + brIds
                        );

                        return response;
                    }
            );
        });
    }
}