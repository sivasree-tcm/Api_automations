package api.framework;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static io.restassured.RestAssured.given;

public class DownloadAtsFrameworkApi {

    private static final String ZIP_DIR =
            "C:\\Users\\hp\\AutomationFrameworkZipped";

    private static final String UNZIP_DIR =
            "C:\\Users\\hp\\AutomationFrameworkUnzipped";

    public static Response downloadFramework(
            Integer projectId,
            Integer userId,
            String automationFramework,
            String projectName,
            String storageType,
            String role,
            String authType
    ) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("projectId", String.valueOf(projectId));
        payload.put("userId", String.valueOf(userId));
        payload.put("automationFramework", automationFramework);
        payload.put("projectName", projectName);
        payload.put("storageType", storageType);

        var req = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(payload);

        if ("VALID".equalsIgnoreCase(authType)) {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        Response response =
                req.when().post("/api/downloadAtsFramework");

        saveAndUnzipIfPresent(response, automationFramework, projectName);

        return response;
    }

    /* -------------------------------------------------- */

    private static void saveAndUnzipIfPresent(
            Response response,
            String framework,
            String projectName
    ) {

        try {

            if (response.getStatusCode() != 200) {
                System.out.println("ℹ Skipping ZIP save – StatusCode: " + response.getStatusCode());
                return;
            }

            File zipDir = new File(ZIP_DIR);
            if (!zipDir.exists()) zipDir.mkdirs();

            String fileName =
                    projectName.replaceAll("\\s+", "_")
                            + "_" + framework + ".zip";

            File zipFile = new File(zipDir, fileName);

            /* ✅ SAVE ZIP */
            try (InputStream is = response.asInputStream();
                 FileOutputStream fos = new FileOutputStream(zipFile)) {

                byte[] buffer = new byte[8192];
                int len;

                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }

            System.out.println("📦 ZIP Saved → " + zipFile.getAbsolutePath());

            /* ✅ UNZIP */
            Path targetDir = Paths.get(
                    UNZIP_DIR,
                    zipFile.getName().replace(".zip", "")
            );

            unzip(zipFile, targetDir);

            System.out.println("✅ ZIP Extracted → " + targetDir);

        } catch (Exception e) {
            throw new RuntimeException("❌ ZIP Save / Unzip Failed", e);
        }
    }

    /* -------------------------------------------------- */

    private static void unzip(File zipFile, Path targetDir) throws IOException {

        Files.createDirectories(targetDir);

        try (ZipInputStream zis =
                     new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                Path filePath = targetDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {

                    Files.createDirectories(filePath.getParent());

                    Files.copy(
                            zis,
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        }
    }
}
