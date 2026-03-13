package tests.organization;

import api.organization.GetOrganizationsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.OrganizationStore;
import utils.TokenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOrganizationsTest extends BaseTest {

    public void getOrganizationsApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/organization/getOrganizations.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ getOrganizations.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // Dynamic userId from token
                        Integer userId = TokenUtil.getUserId(tc.getRole());

                        Map<String, Object> request = new HashMap<>();
                        request.put("userId", userId);

                        // Important for report payload visibility
                        tc.setRequest(request);

                        System.out.println("📦 Get Organizations Payload → " + request);

                        Response response =
                                GetOrganizationsApi.getOrganizations(
                                        request,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Get Organizations Failed → Status: "
                                            + response.getStatusCode()
                                            + " | Body: " + response.asString()
                            );
                        }

                        List<Map<String, Object>> orgs =
                                response.jsonPath().getList("data");

                        if (orgs == null || orgs.isEmpty()) {
                            throw new RuntimeException("❌ Organization list empty.");
                        }

                        boolean found = false;

                        for (Map<String, Object> org : orgs) {

                            String name = (String) org.get("clientName");

                            if ("Tickingminds".equalsIgnoreCase(name)) {

                                Integer id = (Integer) org.get("clientId");

                                OrganizationStore.setOrganization(id, name);

                                System.out.println("✅ Organization Found → " + name + " | ID → " + id);

                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            throw new RuntimeException("❌ Tickingminds organization not found.");
                        }

                        return response;
                    }
            );
        }
    }
}