package tests.export;

import api.export.ExportBRExcelApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExportBRExcelTest extends BaseTest {

    public void exportBRExcelAndValidate() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        // 📁 Excel storage folder (create only if missing)
        Path downloadDir = Paths.get("target/excel-downloads/exportedBR");
        try {
            Files.createDirectories(downloadDir);
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to create download directory", e);
        }

        // 📊 Folder size BEFORE download
        long sizeBefore = getFolderSize(downloadDir);

        ConnectionReport testData =
                JsonUtils.readJson(
                        "testdata/export/exportbrexcel.json",
                        ConnectionReport.class
                );

        ConnectionReport.TestCase tc =
                testData.getTestCases().get(0);

        tc.setExpectedStatusCode(200); // 🔥 REQUIRED

        // ✅ 1. Set request payload for report
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("projectId", projectId);
        requestPayload.put("userId", userId);
        tc.setRequest(requestPayload);

        ApiTestExecutor.execute(
                testData.getScenario(),
                tc,
                () -> {

                    Response response =
                            ExportBRExcelApi.downloadBRExcel(
                                    projectId,
                                    userId,
                                    tc.getRole(),
                                    tc.getAuthType()
                            );

                    // 🕒 Timestamped filename
                    String timestamp =
                            new SimpleDateFormat("yyyyMMdd_HHmmss")
                                    .format(new Date());

                    String fileName =
                            "business_requirement_" + timestamp + ".xlsx";

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

                    // 📊 Folder size AFTER download
                    long sizeAfter = getFolderSize(downloadDir);

                    // ✅ 3. Folder size validation
                    Assert.assertTrue(
                            sizeAfter > sizeBefore,
                            "Folder size did not increase after Excel download"
                    );

                    // ✅ 4. File existence check
                    Assert.assertTrue(
                            Files.exists(excelPath),
                            "Excel file not created"
                    );

                    // 📢 Report-friendly logs
                    System.out.println("📁 Excel stored at : " + excelPath);
                    System.out.println("📊 Folder size before: " + sizeBefore + " bytes");
                    System.out.println("📊 Folder size after : " + sizeAfter + " bytes");
                    System.out.println("📄 Excel file name   : " + fileName);

                    return response;
                }
        );
    }

    // 🔧 Utility: calculate folder size
    private long getFolderSize(Path path) {
        try {
            return Files.walk(path)
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new RuntimeException(
                    "❌ Failed to calculate folder size for: " + path,
                    e
            );
        }
    }
}