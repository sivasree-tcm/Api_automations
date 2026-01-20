package report;

import java.util.List;

public class ReportData {
    public ReportSummary summary;
    public List<ReportTest> tests;


        public void setSummary(ReportSummary summary) {
            this.summary = summary;
        }

        public void setTests(List<ReportTest> tests) {
            this.tests = tests;
        }
    }

