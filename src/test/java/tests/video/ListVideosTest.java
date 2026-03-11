package tests.video;

import api.video.ListVideosApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.Report;
import report.ApiTestExecutor;
import utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListVideosTest extends BaseTest {

    public void listVideosApiTest() {

        Integer userId = TokenUtil.getUserId();

        Report testData = JsonUtils.readJson(
                "testdata/video/listVideos.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ listVideos.json missing or invalid.");
        }

        Report.TestCase tc =
                testData.getTestCases().get(0);

        Map<String, Object> request = new HashMap<>();

        request.put("userId", userId);

        // ✅ show payload in report
        tc.setRequest(request);

        tc.setTcId("LIST_VIDEOS_" + userId);
        tc.setName("List Tutorial Videos");

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            ListVideosApi.listVideos(
                                    request,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    if (response == null) {
                        throw new RuntimeException("❌ API returned NULL response.");
                    }

                    if (response.getStatusCode() != 200) {
                        throw new RuntimeException(
                                "❌ Failed to fetch videos. Status → "
                                        + response.getStatusCode()
                        );
                    }

                    System.out.println("📦 List Videos Response → ");
                    System.out.println(response.asPrettyString());

                    Boolean success =
                            response.jsonPath().getBoolean("success");

                    if (success == null || !success) {
                        throw new RuntimeException("❌ success flag is false.");
                    }

                    String folder =
                            response.jsonPath().getString("folder");

                    if (folder == null || folder.isBlank()) {
                        throw new RuntimeException("❌ folder missing in response.");
                    }

                    List<Map<String, Object>> videos =
                            response.jsonPath().getList("videos");

                    if (videos == null || videos.isEmpty()) {
                        throw new RuntimeException("❌ No videos returned.");
                    }

                    System.out.println("✅ Video Folder → " + folder);
                    System.out.println("✅ Total Videos → " + videos.size());

                    return response;
                }
        );
    }
}