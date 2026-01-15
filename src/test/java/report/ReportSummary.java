package report;

import java.util.List;

public class ReportSummary {

    public int total;
    public int passed;
    public int failed;
    public String passRate;

    public static ReportSummary fromTests(List<ReportTest> tests) {
        ReportSummary s = new ReportSummary();
        s.total = tests.size();
        s.passed = (int) tests.stream()
                .filter(t -> "PASS".equals(t.getStatus())).count();
        s.failed = s.total - s.passed;
        s.passRate = s.total == 0
                ? "0%"
                : (s.passed * 100 / s.total) + "%";
        return s;
    }
}
