package utils;

public class ATSStore {

    private static Integer atsTriggeredTestCaseId;

    public static void set(Integer tcId) {
        atsTriggeredTestCaseId = tcId;
    }

    public static Integer get() {
        return atsTriggeredTestCaseId;
    }

    public static boolean has() {
        return atsTriggeredTestCaseId != null;
    }

    public static void clear() {
        atsTriggeredTestCaseId = null;
    }
}
