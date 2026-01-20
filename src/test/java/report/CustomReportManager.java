package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tests.user.ReportScenario;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomReportManager {
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("🧾 JVM shutdown → Writing custom report");
                writeReport();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }




    /* -------- ADD TEST RESULT -------- */
    private static final Map<String, ReportScenario> SCENARIOS =
            new LinkedHashMap<>();


    public static synchronized void addTest(
            String scenarioName,
            ReportTest test
    ) {
        String scenario =
                (scenarioName == null || scenarioName.isEmpty())
                        ? "Unnamed Scenario"
                        : scenarioName;

        SCENARIOS
                .computeIfAbsent(scenario, ReportScenario::new)
                .addTestCase(test);
    }



    /* -------- WRITE REPORT -------- */

    public static void writeReport() {
        try {
            long total = SCENARIOS.size();

            long passed = SCENARIOS.values().stream()
                    .filter(s -> "PASS".equals(s.getStatus()))
                    .count();

            long failed = total - passed;

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("total", total);
            summary.put("passed", passed);
            summary.put("failed", failed);
            summary.put(
                    "passRate",
                    total == 0 ? "0%" : (passed * 100 / total) + "%"
            );

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("summary", summary);
            report.put("scenarios", SCENARIOS.values());

            new File("target/report").mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(report);

            int passPercent =
                    total == 0 ? 0 : (int) (passed * 100 / total);

            String html = Files.readString(
                    Path.of("src/test/resources/index.html")
            );

            html = html.replace("__REPORT_DATA__", json);
            html = html.replace("__PASS_PERCENT__", String.valueOf(passPercent));

            Files.writeString(
                    Path.of("target/report/index.html"),
                    html
            );

            Files.writeString(
                    Path.of("target/report/test-results.json"),
                    json
            );

            System.out.println("✅ Custom Scenario Report Generated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* -------- STEP UTIL -------- */

    public static Map<String, String> step(
            String type, String label, String value
    ) {
        Map<String, String> s = new LinkedHashMap<>();
        s.put("type", type);
        s.put("label", label);
        s.put("value", value);
        return s;
    }

    public static void addTestResult(String testName, String status, long duration, List<Map<String, String>> steps) {
    }
}
