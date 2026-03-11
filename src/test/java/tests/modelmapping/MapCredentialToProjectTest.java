package tests.modelmapping;

import api.modelmapping.MapCredentialToProjectApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.Map;

public class MapCredentialToProjectTest extends BaseTest {


    public void mapCredentialToProjectApiTest() {

        // ✅ Reuse ConnectionReport
        Report testData =
                JsonUtils.readJson(
                        "testdata/model/mapCredentialToProject.json",
                        Report.class
                );

        for (Report.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            // 🔁 Dynamic userId
            if ("DYNAMIC_USER".equals(request.get("userId"))) {
                request.put(
                        "userId",
                        TokenUtil.getUserId(tc.getRole())
                );
            }

            // 🔁 Dynamic projectId
            if ("DYNAMIC_PROJECT".equals(request.get("projectId"))) {
                request.put(
                        "projectId",
                        ProjectStore.getProjectId()
                );
            }

            // 🔁 Dynamic credentialId
            if ("DYNAMIC_CREDENTIAL".equals(request.get("credential_id"))) {
                request.put(
                        "credential_id",
                        CredentialStore.getCredentialId()
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> MapCredentialToProjectApi.mapCredential(
                            request,
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
