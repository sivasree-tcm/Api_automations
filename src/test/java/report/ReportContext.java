package report;

public class ReportContext {

    private static final ThreadLocal<String> CURRENT_SCENARIO =
            new ThreadLocal<>();

    private static final ThreadLocal<ReportTest> CURRENT_TEST =
            new ThreadLocal<>();

    public static void startScenario(String scenarioName) {
        CURRENT_SCENARIO.set(scenarioName);
    }

    public static void startTest(String testName) {
        CURRENT_TEST.set(new ReportTest(testName));
    }

    // ✅ ADD THIS METHOD (YOUR ERROR FIX)
    public static void setTest(ReportTest test) {
        CURRENT_TEST.set(test);
    }

    public static ReportTest getTest() {
        return CURRENT_TEST.get();
    }

    public static void endTest() {
        ReportTest test = CURRENT_TEST.get();
        String scenario = CURRENT_SCENARIO.get();

        if (test != null && scenario != null) {
            CustomReportManager.addTest(scenario, test);
        }

        CURRENT_TEST.remove();
    }

    public static void endScenario() {
        CURRENT_SCENARIO.remove();
    }
}
