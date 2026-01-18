package tests.user;

import report.ReportTest;
import java.util.ArrayList;
import java.util.List;

public class ReportScenario {

    private final String name;
    private final List<ReportTest> testCases = new ArrayList<>();

    public ReportScenario(String name) {
        this.name = name;
    }

    public void addTestCase(ReportTest test) {
        testCases.add(test);
    }

    // ===== COMPUTED FIELDS =====

    public String getName() {
        return name;
    }

    public String getStatus() {
        return testCases.stream()
                .anyMatch(t -> "FAIL".equals(t.getStatus()))
                ? "FAIL" : "PASS";
    }

    public String getDuration() {
        long total = testCases.stream()
                .mapToLong(t ->
                        Long.parseLong(
                                t.getDuration().replace(" ms", "")
                        )
                ).sum();
        return total + " ms";
    }

    public List<ReportTest> getTestCases() {
        return testCases;
    }
}
