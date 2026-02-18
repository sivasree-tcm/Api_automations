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

    public void fetchLlmCredentials() {

        // 🔹 Load generic test data (only for reporting)
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getLlmCredentials.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                new ConnectionReport.TestCase(
                        testData.getTestCases().get(0)
                );
        System.out.println("getllm");

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

                    // ✅ Always validate response first
                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch LLM credentials. Status: "
                                        + response.getStatusCode()
                        );
                    }

                    Object body = response.jsonPath().get("$");

                    List<Map<String, Object>> creds = new java.util.ArrayList<>();

                    // ✅ Handle BOTH list and object safely
                    if (body instanceof List<?>) {
                        creds = response.jsonPath().getList("$");
                    }
                    else if (body instanceof Map<?, ?>) {
                        System.out.println(
                                "⚠️ Credentials API returned object instead of list:"
                        );
                        response.prettyPrint();
                        return response; // no credentials to store
                    }
                    else {
                        throw new RuntimeException(
                                "❌ Unexpected response structure from GetLlmCredentials API"
                        );
                    }

                    // 🔍 Extract ACTIVE Bedrock credential
                    for (Map<String, Object> cred : creds) {

                        if (cred == null) continue;

                        String provider =
                                String.valueOf(cred.get("provider"));

                        int isActive =
                                cred.get("is_active") == null ? 0 :
                                        ((Number) cred.get("is_active")).intValue();

                        if ("openai-api_key".equalsIgnoreCase(provider)
                                && isActive == 1) {

                            Integer credentialId =
                                    cred.get("credential_id") == null ? null :
                                            ((Number) cred.get("credential_id")).intValue();

                            if (credentialId != null) {
                                CredentialStore.setCredentialId(credentialId);
                                System.out.println(
                                        "✅ Stored Bedrock Credential ID → " + credentialId
                                );
                                break;
                            }
                        }
                    }

                    return response;
                }
        );

    }}
