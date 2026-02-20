package api.framework;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import java.io.*;
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

    // ✅ CRITICAL SAFETY LIMIT (Prevents infinite downloads)
    private static final long MAX_ZIP_SIZE =
            300L * 1024 * 1024; // 300 MB

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

        File zipFile = null;

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

            zipFile = new File(zipDir, fileName);

            // ✅ CLEAN OLD ZIP (Prevents corruption & appending)
            if (zipFile.exists()) {
                System.out.println("♻ Removing existing ZIP → " + zipFile.getAbsolutePath());
                Files.delete(zipFile.toPath());
            }

            long contentLength = extractContentLength(response);

            byte[] buffer = new byte[65536]; // ⭐ 64KB buffer (FASTER)
            long totalBytes = 0;
            int len;

            /* ✅ SAFE STREAM DOWNLOAD */
            try (InputStream is = response.asInputStream();
                 FileOutputStream fos = new FileOutputStream(zipFile)) {

                while ((len = is.read(buffer)) > 0) {

                    fos.write(buffer, 0, len);
                    totalBytes += len;

                    // ✅ HARD SAFETY CUT (CRITICAL)
                    if (totalBytes > MAX_ZIP_SIZE) {
                        throw new RuntimeException(
                                "❌ ZIP exceeded safety limit (" + MAX_ZIP_SIZE + "). Stream likely corrupted."
                        );
                    }

                    // ✅ CONTENT-LENGTH TERMINATION (IF AVAILABLE)
                    if (contentLength > 0 && totalBytes >= contentLength) {
                        break;
                    }
                }
            }

            if (totalBytes == 0) {
                throw new RuntimeException("❌ Downloaded ZIP is EMPTY.");
            }

            System.out.println("📦 ZIP Saved → " + zipFile.getAbsolutePath());
            System.out.println("📦 Total Bytes Written → " + totalBytes);

            /* ✅ UNZIP */
            Path targetDir = Paths.get(
                    UNZIP_DIR,
                    zipFile.getName().replace(".zip", "")
            );

            unzipUsingZipFile(zipFile, targetDir);

            System.out.println("✅ ZIP Extracted → " + targetDir);

        } catch (Exception e) {

            if (zipFile != null && zipFile.exists()) {
                System.out.println("🧹 Cleaning corrupt ZIP → " + zipFile.getAbsolutePath());
                zipFile.delete();
            }

            throw new RuntimeException("❌ ZIP Save / Unzip Failed", e);
        }
    }

    /* -------------------------------------------------- */

    private static long extractContentLength(Response response) {

        try {
            String header = response.getHeader("Content-Length");

            if (header != null) {
                long length = Long.parseLong(header);
                System.out.println("📏 Content-Length → " + length);
                return length;
            }

        } catch (Exception ignored) {
        }

        return -1;
    }

    /* -------------------------------------------------- */

    private static void unzipUsingZipFile(File zipFile, Path targetDir) throws IOException {

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

                    try (InputStream is = zf.getInputStream(entry)) {
                        Files.copy(
                                is,
                                filePath,
                                StandardCopyOption.REPLACE_EXISTING
                        );
                    }
                }
            }
        }
    }
}
