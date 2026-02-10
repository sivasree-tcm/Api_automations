package tests.project;

import api.project.GetLlmCredentialsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.CredentialStore;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLlmCredentialsTest extends BaseTest {
@Test
    public void fetchLlmCredentials() {

        // 🔹 Load generic test data (only for reporting)
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/GetLlmCredentials.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );

        // 🔹 Build request dynamically
        Map<String, Object> request = new HashMap<>();
        request.put(
                "userId",
                TokenUtil.getUserId(tc.getRole())
        );

        tc.setRequest(request);
        tc.setTcId("GET_LLM_CREDENTIALS");
        tc.setName("Get LLM Credentials");

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            GetLlmCredentialsApi.getLlmCredentials(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    // 🔍 Extract ACTIVE Bedrock credential
                    List<Map<String, Object>> creds =
                            response.jsonPath().getList("$");

                    for (Map<String, Object> cred : creds) {

                        String provider =
                                String.valueOf(cred.get("provider"));

                        int isActive =
                                ((Number) cred.get("is_active")).intValue();

                        if ("bedrock".equalsIgnoreCase(provider)
                                && isActive == 1) {

                            int credentialId =
                                    ((Number) cred.get("credential_id"))
                                            .intValue();

                            CredentialStore.setCredentialId(credentialId);
                            break;
                        }
                    }

                    return response;
                }
        );
    }
}
