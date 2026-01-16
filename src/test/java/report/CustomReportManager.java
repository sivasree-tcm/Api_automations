package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

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
//        test.put("duration", formatDuration(durationMillis));
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

        long total = (long) summary.get("total");
        long passed = (long) summary.get("passed");
        long failed = (long) summary.get("failed");

        int passPercent = total == 0 ? 0 : (int) (passed * 100 / total);

        StringBuilder rows = new StringBuilder();
        for (Map<String, Object> test : tests) {
            String status = test.get("status").toString();
            rows.append("<tr>")
                    .append("<td>").append(test.get("name")).append("</td>")
                    .append("<td class='").append(status.toLowerCase()).append("'>")
                    .append(status)
                    .append("</td>")
                    .append("<td>").append(test.get("duration")).append("</td>")
                    .append("</tr>");
        }

        String html =
                "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                        "<title>API Intelligence</title>" +

                        // ---------- DARK THEME CSS ----------
                        "<style>" +
                        "body{margin:0;font-family:Segoe UI;background:#0b0f1a;color:#fff}" +
                        ".container{padding:30px}" +
                        ".header{font-size:22px;color:#00e5ff;margin-bottom:20px}" +
                        ".cards{display:flex;gap:20px;margin-bottom:30px}" +
                        ".card{flex:1;background:#12182b;padding:20px;border-radius:12px}" +
                        ".card h2{margin:0;font-size:28px}" +
                        ".pass{color:#00ff9c;font-weight:bold}" +
                        ".fail{color:#ff3366;font-weight:bold}" +
                        ".chart{width:180px;height:180px;border-radius:50%;" +
                        "background:conic-gradient(#00ff9c " + passPercent +
                        "%, #ff3366 0)}" +
                        "table{width:100%;border-collapse:collapse;background:#12182b}" +
                        "th,td{padding:12px;border-bottom:1px solid #1e253f}" +
                        "th{text-align:left;color:#9aa4ff}" +
                        "</style></head>" +

                        "<body><div class='container'>" +

                        "<div class='header'>API INTELLIGENCE</div>" +

                        "<div class='cards'>" +
                        "<div class='card'><p>Total</p><h2>" + total + "</h2></div>" +
                        "<div class='card'><p>Passed</p><h2 class='pass'>" + passed + "</h2></div>" +
                        "<div class='card'><p>Failed</p><h2 class='fail'>" + failed + "</h2></div>" +
                        "<div class='card'><p>Stability</p><h2>" + passPercent + "%</h2></div>" +
                        "</div>" +

                        "<div style='display:flex;gap:40px'>" +
                        "<div class='chart'></div>" +

                        "<table>" +
                        "<tr><th>Test Name</th><th>Status</th><th>Duration</th></tr>" +
                        rows +
                        "</table>" +
                        "</div>" +

                        "</div></body></html>";

        Files.writeString(Path.of("target/report/index.html"), html);
    }

}
