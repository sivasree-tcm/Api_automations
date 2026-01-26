package tests.queue;

import api.queue.DeleteGenerationQueueApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;

public class DeleteGenerationQueueTest extends BaseTest {

    @Test
    public void deleteGenerationQueueTest() {

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/queueData/deleteGenerationQueue.json",
                        ConnectionReport.class
                );

        for (ConnectionReport.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> DeleteGenerationQueueApi.deleteQueue(
                            tc.getRequest(),
                            tc.getRole(),
                            tc.getAuthType()
                    )
            );
        }
    }
}
