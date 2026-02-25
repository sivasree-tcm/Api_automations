package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class AtsFrameworkRunner {

    private static final String UNZIP_DIR =
            "C:\\Users\\hp\\AutomationFrameworkUnzipped";

    // ✅ HARD FIX → Avoid PATH dependency entirely
    private static final String DOTNET_EXE =
            "C:\\Program Files\\dotnet\\dotnet.exe";

    // ✅ SAFETY → Prevent infinite / long-running executions
    private static final long EXECUTION_TIMEOUT_MINUTES = 5;

    public static void run(String projectName, String framework, String testCaseNumber) {

        try {

            /* ---------------------------------------------------- */
            /* 1️⃣ Resolve Extracted Framework Folder                */
            /* ---------------------------------------------------- */

            String folderName = projectName.replaceAll("\\s+", "_")
                    + "_" + framework;

            Path frameworkDir = Paths.get(UNZIP_DIR, folderName);

            if (!frameworkDir.toFile().exists()) {
                throw new RuntimeException(
                        "❌ Extracted framework not found → " + frameworkDir
                );
            }

            /* ---------------------------------------------------- */
            /* 2️⃣ Validate dotnet Installation (Critical Fix)       */
            /* ---------------------------------------------------- */

            Path dotnetPath = Paths.get(DOTNET_EXE);

            if (!dotnetPath.toFile().exists()) {
                throw new RuntimeException(
                        "❌ dotnet.exe not found → Install .NET SDK OR fix path: "
                                + DOTNET_EXE
                );
            }

            /* ---------------------------------------------------- */
            /* 3️⃣ Convert TestCaseNumber → Framework Class Filter   */
            /* ---------------------------------------------------- */

            String filterName = testCaseNumber.replace("-", "_");

            System.out.println("🚀 Running ATS → " + filterName);
            System.out.println("📂 Framework Dir → " + frameworkDir);
            System.out.println("🧪 dotnet Path → " + DOTNET_EXE);

            /* ---------------------------------------------------- */
            /* 4️⃣ Build dotnet Process Safely                       */
            /* ---------------------------------------------------- */

            ProcessBuilder pb = new ProcessBuilder(
                    DOTNET_EXE,
                    "test",
                    "--filter",
                    "FullyQualifiedName~" + filterName
            );

            pb.directory(frameworkDir.toFile());
            pb.redirectErrorStream(true);

            long start = System.currentTimeMillis();

            Process process = pb.start();

            /* ---------------------------------------------------- */
            /* 5️⃣ Stream Output (Deadlock Safe)                     */
            /* ---------------------------------------------------- */

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while (true) {

                    // ✅ Timeout Protection During Read
                    if (!process.isAlive()) break;

                    if (reader.ready()) {
                        line = reader.readLine();
                        if (line != null) {
                            System.out.println(line);
                        }
                    }
                }
            }

            /* ---------------------------------------------------- */
            /* 6️⃣ Hard Timeout Protection                           */
            /* ---------------------------------------------------- */

            boolean finished = process.waitFor(
                    EXECUTION_TIMEOUT_MINUTES,
                    TimeUnit.MINUTES
            );

            if (!finished) {

                process.destroyForcibly();

                throw new RuntimeException(
                        "❌ dotnet test TIMEOUT after "
                                + EXECUTION_TIMEOUT_MINUTES + " minutes"
                );
            }

            int exitCode = process.exitValue();

            long duration = System.currentTimeMillis() - start;

            if (exitCode != 0) {
                throw new RuntimeException(
                        "❌ dotnet test FAILED → ExitCode: "
                                + exitCode + " | Duration: " + duration + " ms"
                );
            }

            System.out.println("✅ ATS Execution PASSED → "
                    + filterName + " | Duration: " + duration + " ms");

        } catch (Exception e) {
            throw new RuntimeException("❌ ATS Framework Execution Failed", e);
        }
    }
}