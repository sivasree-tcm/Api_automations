package tests.export;

import api.export.ExportTCExcelApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.GeneratedTSStore;
import utils.JsonUtils;
import utils.TokenUtil;

import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportTCExcelTest extends BaseTest {

    public void exportTCExcelForGeneratedTS() {

        if (!GeneratedTSStore.hasTS()) {
            throw new RuntimeException("❌ No generated TS available for TC export");
        }

        Integer userId = TokenUtil.getUserId();

        Path downloadDir =
                Paths.get("target/excel-downloads/exportedTC");

        try {
            Files.createDirectories(downloadDir);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to create TC export directory", e);
        }

        long sizeBefore = getFolderSize(downloadDir);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/export/exporttcexcel.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                testData.getTestCases().get(0);

        for (Integer tsId : GeneratedTSStore.getAll()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ Request payload for report
                        Map<String, Object> requestPayload = new HashMap<>();
                        requestPayload.put(
                                "testScenarioId",
                                Collections.singletonList(tsId)
                        );
                        requestPayload.put("userId", userId);
                        tc.setRequest(requestPayload);

                        Response response =
                                ExportTCExcelApi.downloadTCExcel(
                                        Collections.singletonList(tsId),
                                        userId,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );

                        String timestamp =
                                new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date());

                        String fileName =
                                "test_cases_TS_" + tsId + "_" + timestamp + ".xlsx";

                        Path excelPath =
                                downloadDir.resolve(fileName);

                        try {
                            Files.write(excelPath, response.asByteArray());
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "❌ Failed to write TC Excel file",
                                    e
                            );
                        }

                        long sizeAfter = getFolderSize(downloadDir);

                        Assert.assertTrue(
                                sizeAfter > sizeBefore,
                                "TC Excel folder size did not increase"
                        );

                        Assert.assertTrue(
                                Files.exists(excelPath),
                                "TC Excel file not created"
                        );

                        System.out.println("📁 TC Excel stored at : " + excelPath);
                        System.out.println("📊 Folder size before: " + sizeBefore);
                        System.out.println("📊 Folder size after : " + sizeAfter);
                        System.out.println("📄 TC Excel file     : " + fileName);

                        return response;
                    }
            );
        }
    }

    private long getFolderSize(Path path) {
        try {
            return Files.walk(path)
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (Exception e) {
                            return 0L;
                        }
                    })
                    .sum();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to calculate folder size", e);
        }
    }
}