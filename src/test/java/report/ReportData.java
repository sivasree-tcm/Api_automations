package report;

import java.util.List;
import tests.user.ReportScenario;

public class ReportData {

    private ReportSummary summary;
    private List<ReportScenario> scenarios;

    public ReportSummary getSummary() {
        return summary;
    }

    public void setSummary(ReportSummary summary) {
        this.summary = summary;
    }

    public List<ReportScenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<ReportScenario> scenarios) {
        this.scenarios = scenarios;
    }
}