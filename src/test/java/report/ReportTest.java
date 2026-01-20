package report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportTest {

    private final String name;
    private String status = "PASS";
    private long duration;
    private final List<Map<String, String>> steps = new ArrayList<>();

    private long startTime;

    public ReportTest(String name) {
        this.name = name;
        this.startTime = System.currentTimeMillis();
    }

    public void addStep(ReportStep step) {
        steps.add(CustomReportManager.step(
                step.getType(),
                step.getLabel(),
                step.getValue()
        ));
    }

    public void markPassed(String msg) {
        this.status = "PASS";
        finish();
    }

    public void markFailed(String msg) {
        this.status = "FAIL";
        addStep(new ReportStep("Fail", "Failure Reason", msg));
        finish();
    }

    private void finish() {
        this.duration = System.currentTimeMillis() - startTime;
    }

    // ===== GETTERS (USED BY JSON) =====
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDuration() {
        return duration + " ms";
    }

    public List<Map<String, String>> getSteps() {
        return steps;
    }
}
