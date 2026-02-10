package tests.project;

import api.project.MapCredentialToProjectApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.Map;

public class MapCredentialToProjectTest extends BaseTest {

    @Test
    public void mapCredentialToProjectApiTest() {

        // ✅ Reuse ConnectionReport
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/mapCredentialToProject.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

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
