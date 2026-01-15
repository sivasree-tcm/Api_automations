package report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomReportManager {

    private static final String REPORT_PATH =
            "target/report/test-results.json";

    // Thread-safe ID generator
    private static final AtomicInteger ID = new AtomicInteger(1);

    // All test results
    private static final List<Map<String, Object>> tests =
            Collections.synchronizedList(new ArrayList<>());

    /**
     * Add a full test result (PASS / FAIL)
     */
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

    /**
     * Write final JSON report
     */
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

            File file = new File(REPORT_PATH);
            file.getParentFile().mkdirs();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(report, writer);
            }

            System.out.println("✅ Custom report JSON generated at: " + REPORT_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- UTIL METHODS ----------

    public static Map<String, String> step(
            String type,
            String label,
            String value
    ) {
        Map<String, String> s = new LinkedHashMap<>();
        s.put("type", type);   // Info / Pass / Fail
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
