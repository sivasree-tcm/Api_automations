package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomReportManager {

    private static final String REPORT_JSON =
            "target/report/test-results.json";

    private static final String REPORT_HTML =
            "target/report/index.html";

    // Thread-safe ID generator
    private static final AtomicInteger ID = new AtomicInteger(1);

    // All test results
    private static final List<Map<String, Object>> tests =
            Collections.synchronizedList(new ArrayList<>());

    /* ---------------- ADD TEST RESULT ---------------- */

    public static void addTestResult(
            String testName,
            String status,
            long durationMillis,
            List<Map<String, String>> steps
    ) {
        Map<String, Object> test = new LinkedHashMap<>();
        test.put("id", ID.getAndIncrement());
        test.put("name", testName);
        test.put("status", status);
        test.put("duration", formatDuration(durationMillis));
        test.put("steps", steps);

        tests.add(test);
    }

    /* ---------------- WRITE REPORT ---------------- */

    public static void writeReport() {
        try {
            long passed = tests.stream()
                    .filter(t -> "PASS".equals(t.get("status")))
                    .count();

            long failed = tests.size() - passed;

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("total", tests.size());
            summary.put("passed", passed);
            summary.put("failed", failed);
            summary.put(
                    "passRate",
                    tests.isEmpty()
                            ? "0%"
                            : (passed * 100 / tests.size()) + "%"
            );

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("summary", summary);
            report.put("tests", tests);

            // ---------- WRITE JSON ----------
            File jsonFile = new File(REPORT_JSON);
            jsonFile.getParentFile().mkdirs();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(report, writer);
            }

            // ---------- WRITE STATIC HTML (NEW) ----------
            generateStaticHtml(report);

            System.out.println("✅ Custom JSON & STATIC HTML report generated");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- HTML GENERATOR (NEW) ---------------- */

    private static void generateStaticHtml(Map<String, Object> report)
            throws Exception {

        Map<String, Object> summary =
                (Map<String, Object>) report.get("summary");

        List<Map<String, Object>> tests =
                (List<Map<String, Object>>) report.get("tests");

        StringBuilder rows = new StringBuilder();

        for (Map<String, Object> test : tests) {
            String status = test.get("status").toString();

            rows.append("<tr>")
                    .append("<td>").append(test.get("id")).append("</td>")
                    .append("<td>").append(test.get("name")).append("</td>")
                    .append("<td class='").append(status.toLowerCase()).append("'>")
                    .append(status)
                    .append("</td>")
                    .append("<td>").append(test.get("duration")).append("</td>")
                    .append("</tr>");
        }

        String html =
                "<!DOCTYPE html>" +
                        "<html><head>" +
                        "<meta charset='UTF-8'>" +
                        "<title>Custom API Automation Report</title>" +
                        "<style>" +
                        "body{font-family:Arial;background:#f4f6f8;padding:20px}" +
                        ".card{background:#fff;padding:20px;border-radius:8px}" +
                        "table{width:100%;border-collapse:collapse;margin-top:15px}" +
                        "th,td{border:1px solid #ddd;padding:8px;text-align:left}" +
                        "th{background:#f0f0f0}" +
                        ".pass{color:green;font-weight:bold}" +
                        ".fail{color:red;font-weight:bold}" +
                        "</style>" +
                        "</head><body>" +

                        "<div class='card'>" +
                        "<h2>API Automation Execution Report</h2>" +
                        "<p><b>Total:</b> " + summary.get("total") + "</p>" +
                        "<p class='pass'><b>Passed:</b> " + summary.get("passed") + "</p>" +
                        "<p class='fail'><b>Failed:</b> " + summary.get("failed") + "</p>" +
                        "<p><b>Pass Rate:</b> " + summary.get("passRate") + "</p>" +

                        "<table>" +
                        "<tr><th>ID</th><th>Test Name</th><th>Status</th><th>Duration</th></tr>" +
                        rows +
                        "</table>" +

                        "</div>" +
                        "</body></html>";

        Files.writeString(Path.of(REPORT_HTML), html);
    }

    /* ---------------- UTIL METHODS ---------------- */

    public static Map<String, String> step(
            String type,
            String label,
            String value
    ) {
        Map<String, String> s = new LinkedHashMap<>();
        s.put("type", type);
        s.put("label", label);
        s.put("value", value);
        return s;
    }

    private static String formatDuration(long millis) {
        long sec = millis / 1000;
        long ms = millis % 1000;
        return sec + "." + ms + "s";
    }

    public static synchronized void addTest(ReportTest reportTest) {

        if (reportTest == null) return;

        Map<String, Object> test = new LinkedHashMap<>();
        test.put("id", ID.getAndIncrement());
        test.put("name", reportTest.getName());
        test.put("status", reportTest.getStatus());
        test.put("duration", reportTest.getDuration());
        test.put("steps", reportTest.getSteps());

        tests.add(test);
    }
}
