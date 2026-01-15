package report;

public class ReportContext {

    public static ReportTest startTest(String testName) {
        return new ReportTest(testName);
    }
}
