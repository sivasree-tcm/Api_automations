package report;

public class ReportStep {

    private String type;
    private String label;
    private String value;

    public ReportStep(String type, String label, String value) {
        this.type = type;
        this.label = label;
        this.value = value;
    }

    // getters
    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
}
