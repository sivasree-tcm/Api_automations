package models.configuration;

import java.util.List;
import java.util.Map;

public class ConfigRequest {

    private String userId;
    private String projectId;

    // ---- save config fields ----
    private String model;
    private String refBR;
    private String testDataMCP;
    private String atsBranch;

    public String getAzureDevOps() {
        return azureDevOps;
    }

    public void setAzureDevOps(String azureDevOps) {
        this.azureDevOps = azureDevOps;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRefBR() {
        return refBR;
    }

    public void setRefBR(String refBR) {
        this.refBR = refBR;
    }

    public String getTestDataMCP() {
        return testDataMCP;
    }

    public void setTestDataMCP(String testDataMCP) {
        this.testDataMCP = testDataMCP;
    }

    public String getAtsBranch() {
        return atsBranch;
    }

    public void setAtsBranch(String atsBranch) {
        this.atsBranch = atsBranch;
    }

    public String getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(String dbConfig) {
        this.dbConfig = dbConfig;
    }

    public Map<String, Object> getApiSettings() {
        return apiSettings;
    }

    public void setApiSettings(Map<String, Object> apiSettings) {
        this.apiSettings = apiSettings;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public List<Map<String, Object>> getEnvironemntDetails() {
        return environemntDetails;
    }

    public void setEnvironemntDetails(List<Map<String, Object>> environemntDetails) {
        this.environemntDetails = environemntDetails;
    }

    private String azureDevOps;
    private String dbConfig;
    private Map<String, Object> apiSettings;
    private Integer threshold;

    // ---- add db info (INLINE, no separate class) ----
    private List<Map<String, Object>> environemntDetails;

    // getters & setters
}
