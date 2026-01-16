package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomReportManager {

    private static final AtomicInteger ID = new AtomicInteger(1);
    private static final List<Map<String, Object>> tests =
            Collections.synchronizedList(new ArrayList<>());

    /* -------- ADD TEST RESULT -------- */

    public static void addTestResult(
            String name,
            String status,
            long duration,
            List<Map<String, String>> steps
    ) {
        if (duration < 0) duration = 0;

        Map<String, Object> test = new LinkedHashMap<>();
        test.put("id", ID.getAndIncrement());
        test.put("name", name);
        test.put("status", status);
        test.put("duration", duration + " ms");
        test.put("steps", steps);

        tests.add(test);
    }
    public static synchronized void addTest(ReportTest reportTest) {

        if (reportTest == null) return;

        Map<String, Object> test = new LinkedHashMap<>();
//        test.put("id", reportTest.getId());
        test.put("name", reportTest.getName());
        test.put("status", reportTest.getStatus());
        test.put("duration", reportTest.getDuration());
        test.put("steps", reportTest.getSteps());

        tests.add(test);
    }

    /* -------- WRITE REPORT -------- */

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
            summary.put("passRate",
                    tests.isEmpty() ? "0%" : (passed * 100 / tests.size()) + "%");

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("summary", summary);
            report.put("tests", tests);

            new File("target/report").mkdirs();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(report);

            int passPercent = tests.isEmpty() ? 0 :
                    (int)(passed * 100 / tests.size());

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

            System.out.println("✅ Custom STATIC report generated");

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
}
