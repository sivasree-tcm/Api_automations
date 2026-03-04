package tests.logs;

import api.logs.DownloadLogFileApi;
import base.BaseTest;
import io.restassured.response.Response;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadLogFileTest extends BaseTest {

    public void downloadLogFileApiTest() {

        Integer userId = TokenUtil.getUserId();

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/logs/downloadLogFile.json",
                ConnectionReport.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ downloadLogFile.json missing or invalid.");
        }

        List<String> logFiles = LogStore.getLogFiles();

        for (String fileName : logFiles) {

            for (ConnectionReport.TestCase tc : testData.getTestCases()) {

                Map<String, Object> request = new HashMap<>();

                request.put("filename", fileName);
                request.put("userId", userId);

                tc.setRequest(request);

                ApiTestExecutor.execute(
                        testData.getScenario() + " | " + fileName,
                        tc,
                        () -> {

                            Response response =
                                    DownloadLogFileApi.downloadLogFile(
                                            request,
                                            tc.getRole(),
                                            tc.getAuthType()
                                    );

                            if (response == null) {
                                throw new RuntimeException("❌ API returned NULL response.");
                            }

                            if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                                throw new RuntimeException(
                                        "❌ Download Log File Failed → Status: "
                                                + response.getStatusCode()
                                                + " | Body: " + response.asString()
                                );
                            }

                            try {

                                File dir = new File("downloads/logs");
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }

                                File file = new File(dir, fileName);

                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(response.asByteArray());
                                fos.close();

                                System.out.println("⬇ Log Downloaded → " + file.getAbsolutePath());

                            } catch (Exception e) {
                                throw new RuntimeException("❌ File Save Failed", e);
                            }

                            return response;
                        }
                );
            }
        }
    }
}