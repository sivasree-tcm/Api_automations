package tests.project;

import api.project.GetProjectProviderConfigApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.JsonUtils;
import utils.TokenUtil;

import java.util.Map;

public class GetProjectProviderConfigTest extends BaseTest {


    public void getProjectProviderConfigApiTest() {

        // ✅ Reuse ConnectionReport
        Report testData =
                JsonUtils.readJson(
                        "testdata/project/getProjectProviderConfig.json",
                        Report.class
                );

        for (Report.TestCase tc : testData.getTestCases()) {

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
