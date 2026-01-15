package report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportTest {

    private final String name;
    private String status = "FAIL";
    private long startTime;
    private long endTime;

    private final List<Map<String, String>> steps = new ArrayList<>();

    // ✅ REQUIRED CONSTRUCTOR
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

    public void markPassed(String message) {
        this.status = "PASS";
        addStep(new ReportStep("Pass", "Result", message));
        this.endTime = System.currentTimeMillis();
    }

    public void markFailed(String message) {
        this.status = "FAIL";
        addStep(new ReportStep("Fail", "Result", message));
        this.endTime = System.currentTimeMillis();
    }

    // ---- getters ----
    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDuration() {
        return (endTime - startTime) + " ms";
    }

    public List<Map<String, String>> getSteps() {
        return steps;
    }
}
