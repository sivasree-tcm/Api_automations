package report;

public class ReportStep {

    private final String type;
    private final String label;
    private final String value;

    public ReportStep(String type, String label, String value) {
        this.type = type;
        this.label = label;
        this.value = value;
    }

    public String getType() { return type; }
    public String getLabel() { return label; }
    public String getValue() { return value; }
}
