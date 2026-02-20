package api.framework;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.restassured.RestAssured.given;

public class DownloadAtsFrameworkApi {

    private static final String ZIP_DIR =
            "C:\\Users\\hp\\AutomationFrameworkZipped";

    private static final String UNZIP_DIR =
            "C:\\Users\\hp\\AutomationFrameworkUnzipped";

    public static Response downloadFramework(
            Map<String, Object> payload,
            String role,
            String authType
    ) {

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

        saveAndUnzipIfPresent(
                response,
                String.valueOf(payload.get("automationFramework")),
                String.valueOf(payload.get("projectName"))
        );

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

            Path zipPath = Paths.get(ZIP_DIR, fileName);

            // ✅ CLEAN OLD ZIP
            if (Files.exists(zipPath)) {
                System.out.println("♻ Removing existing ZIP → " + zipPath);
                Files.delete(zipPath);
            }

            /* =========================================================
               ✅ FAST DOWNLOAD FIX (CRITICAL CHANGE)
               Replaced InputStream streaming with full buffering.
               Matches Postman behaviour → Much faster & stable.
               ========================================================= */

            byte[] bytes = response.asByteArray();   // ⭐ FAST & SAFE
            Files.write(zipPath, bytes);

            System.out.println("📦 ZIP Saved → " + zipPath);
            System.out.println("📦 Total Bytes Written → " + bytes.length);

            /* ✅ UNZIP */
            Path targetDir = Paths.get(
                    UNZIP_DIR,
                    fileName.replace(".zip", "")
            );

            deleteDirectoryIfExists(targetDir);

            unzipUsingZipFile(zipPath.toFile(), targetDir);

            System.out.println("✅ ZIP Extracted → " + targetDir);

        } catch (Exception e) {
            throw new RuntimeException("❌ ZIP Save / Unzip Failed", e);
        }
    }

    /* -------------------------------------------------- */

    private static void deleteDirectoryIfExists(Path path) throws Exception {

        if (!Files.exists(path)) return;

        System.out.println("🧹 Removing existing UNZIP directory → " + path);

        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /* -------------------------------------------------- */

    private static void unzipUsingZipFile(File zipFile, Path targetDir) throws Exception {

        Files.createDirectories(targetDir);

        try (ZipFile zf = new ZipFile(zipFile)) {

            Enumeration<? extends ZipEntry> entries = zf.entries();

            while (entries.hasMoreElements()) {

                ZipEntry entry = entries.nextElement();

                Path filePath = targetDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {

                    Files.createDirectories(filePath.getParent());

                    Files.copy(
                            zf.getInputStream(entry),
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        }
    }
}