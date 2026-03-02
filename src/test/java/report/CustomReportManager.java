package report;

import java.util.*;

public class CustomReportManager {

    /* ======================================================
       STRUCTURE
       Runner → Scenario → TestCases
       ====================================================== */

    public static class Scenario {
        public String name;
        public List<ReportTest> testCases = new ArrayList<>();

        public Scenario(String name) {
            this.name = name;
        }
    }

    // Runner -> Scenario Map
    private static final Map<String,
            Map<String, Scenario>> runners = new LinkedHashMap<>();


    /* ======================================================
       NEW METHOD (USED INTERNALLY)
       ====================================================== */
    public static synchronized void addTest(
            String runnerName,
            String scenarioName,
            ReportTest test
    ) {

        runners
                .computeIfAbsent(runnerName,
                        r -> new LinkedHashMap<>())

                .computeIfAbsent(scenarioName,
                        s -> new Scenario(s))

                .testCases.add(test);
    }


    /* ======================================================
       OLD METHOD (BACKWARD COMPATIBILITY)
       DO NOT REMOVE — MANY CLASSES USE THIS
       ====================================================== */
    public static synchronized void addTest(
            String scenarioName,
            ReportTest test
    ) {
        addTest("DefaultRunner", scenarioName, test);
    }

    /* ======================================================
       STEP HELPER (REQUIRED BY ReportTest)
       ====================================================== */
    public static Map<String, String> step(
            String type,
            String label,
            String value
    ) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("type", type);
        map.put("label", label);
        map.put("value", value);
        return map;
    }
    /* ======================================================
       REPORT DATA FOR HTML
       ====================================================== */
    public static Map<String, Object> buildReportData() {

        Map<String, Object> result = new HashMap<>();
        result.put("runners", runners);
        return result;
    }
}