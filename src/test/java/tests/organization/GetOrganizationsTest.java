package tests.organization;

import api.organization.GetOrganizationsApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import tests.user.ApiTestExecutor;
import utils.*;

import java.util.List;
import java.util.Map;

public class GetOrganizationsTest extends BaseTest {

    public void getOrganizationsApiTest() {

        Report testData = JsonUtils.readJson(
                "testdata/organization/getOrganizations.json",
                Report.class
        );

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response =
                                GetOrganizationsApi.getOrganizations(
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "❌ Get Organizations Failed → "
                                            + response.asString()
                            );
                        }

                        List<Map<String, Object>> orgs =
                                response.jsonPath().getList("data");

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