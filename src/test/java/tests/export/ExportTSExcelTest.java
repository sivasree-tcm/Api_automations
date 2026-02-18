package tests.export;

import api.export.ExportTSExcelApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportTSExcelTest extends BaseTest {

    public void exportTSExcelForGeneratedBRs() {

        Integer userId = TokenUtil.getUserId();

        // 🔹 ONLY BRs that were generated to TS
        List<Integer> generatedBrIds = GeneratedBRStore.getBrIds();

        if (generatedBrIds == null || generatedBrIds.isEmpty()) {
            throw new RuntimeException("❌ No generated BRs found for TS export");
        }

        // 📁 TS Excel storage directory
        Path downloadDir = Paths.get("target/excel-downloads/exportedTS");
        try {
            Files.createDirectories(downloadDir);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to create TS Excel directory", e);
        }

        // 📊 Folder size BEFORE download
        long sizeBefore = getFolderSize(downloadDir);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/export/exporttsexcel.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                testData.getTestCases().get(0);

        tc.setExpectedStatusCode(200); // 🔥 REQUIRED

        for (Integer brId : generatedBrIds) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        // ✅ SET REQUEST PAYLOAD FOR REPORT
                        Map<String, Object> requestPayload = new HashMap<>();
                        requestPayload.put("brId", brId);
                        requestPayload.put("userId", userId);
                        tc.setRequest(requestPayload);

                        Response response =
                                ExportTSExcelApi.downloadTSExcel(
                                        brId,
                                        userId,
                                        tc.getRole(),
                                        tc.getAuthType()
                                );


                        // 🕒 Timestamped filename
                        String timestamp =
                                new SimpleDateFormat("yyyyMMdd_HHmmss")
                                        .format(new Date());

                        String fileName =
                                "test_scenarios_BR_" + brId + "_" + timestamp + ".xlsx";

                        Path excelPath = downloadDir.resolve(fileName);

                        // 💾 Save Excel
                        try {
                            Files.write(excelPath, response.asByteArray());
                        } catch (IOException e) {
                            throw new RuntimeException(
                                    "❌ Failed to write Excel file: " + excelPath,
                                    e
                            );
                        }

                        long sizeAfter = getFolderSize(downloadDir);

                        // ✅ Folder size validation
                        Assert.assertTrue(
                                sizeAfter > sizeBefore,
                                "TS Excel folder size did not increase"
                        );

                        // ✅ File existence
                        Assert.assertTrue(
                                Files.exists(excelPath),
                                "TS Excel file not created for BR " + brId
                        );

                        // 📢 Report logs
                        System.out.println("📁 TS Excel stored at : " + excelPath);
                        System.out.println("📊 Folder size before: " + sizeBefore + " bytes");
                        System.out.println("📊 Folder size after : " + sizeAfter + " bytes");
                        System.out.println("📄 TS Excel file     : " + fileName);

                        return response;
                    }
            );
        }
    }

    // 🔧 Folder size utility
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
            throw new RuntimeException(
                    "❌ Failed to calculate folder size for: " + path,
                    e
            );
        }
    }
}