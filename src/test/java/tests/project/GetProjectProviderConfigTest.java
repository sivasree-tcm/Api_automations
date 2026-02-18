package tests.project;

import api.project.GetProjectProviderConfigApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.Map;

public class GetProjectProviderConfigTest extends BaseTest {


    public void getProjectProviderConfigApiTest() {

        // ✅ Reuse ConnectionReport
        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/project/getProjectProviderConfig.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            Map<String, Object> request =
                    (Map<String, Object>) tc.getRequest();

            // 🔁 Dynamic userId injection
            if ("DYNAMIC_USER".equals(request.get("userId"))) {
                request.put(
                        "userId",
                        TokenUtil.getUserId(tc.getRole())
                );
            }

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> GetProjectProviderConfigApi
                            .getProjectProviderConfig(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            )
            );
        }
    }
}
