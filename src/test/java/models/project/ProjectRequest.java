package models.project;

public class ProjectRequest {

    private String userId;
    private String projectName;
    private String projectDescription;
    private String projectSummary;
    private String projectStartDate;
    private String projectEndDate;
    private String projectCreatedBy;
    private String webFramework;
    private String mobileFrameworks;
    private int autonomous;
    private String projectDomain;
    private String testType;
    private String InsightsBasedOnExistingAssets;
    private String refOrgId;
    private String storageType;
    private String connections;
    private String devopsProjectName;
    private String teams;

    // Getters & Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDescription() { return projectDescription; }
    public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }

    public String getProjectSummary() { return projectSummary; }
    public void setProjectSummary(String projectSummary) { this.projectSummary = projectSummary; }

    public String getProjectStartDate() { return projectStartDate; }
    public void setProjectStartDate(String projectStartDate) { this.projectStartDate = projectStartDate; }

    public String getProjectEndDate() { return projectEndDate; }
    public void setProjectEndDate(String projectEndDate) { this.projectEndDate = projectEndDate; }

    public String getProjectCreatedBy() { return projectCreatedBy; }
    public void setProjectCreatedBy(String projectCreatedBy) { this.projectCreatedBy = projectCreatedBy; }

    public String getWebFramework() { return webFramework; }
    public void setWebFramework(String webFramework) { this.webFramework = webFramework; }

    public String getMobileFrameworks() { return mobileFrameworks; }
    public void setMobileFrameworks(String mobileFrameworks) { this.mobileFrameworks = mobileFrameworks; }

    public int getAutonomous() { return autonomous; }
    public void setAutonomous(int autonomous) { this.autonomous = autonomous; }

    public String getProjectDomain() { return projectDomain; }
    public void setProjectDomain(String projectDomain) { this.projectDomain = projectDomain; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public String getInsightsBasedOnExistingAssets() { return InsightsBasedOnExistingAssets; }
    public void setInsightsBasedOnExistingAssets(String insights) {
        InsightsBasedOnExistingAssets = insights;
    }

    public String getRefOrgId() { return refOrgId; }
    public void setRefOrgId(String refOrgId) { this.refOrgId = refOrgId; }

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }

    public String getConnections() { return connections; }
    public void setConnections(String connections) { this.connections = connections; }

    public String getDevopsProjectName() { return devopsProjectName; }
    public void setDevopsProjectName(String devopsProjectName) {
        this.devopsProjectName = devopsProjectName;
    }

    public String getTeams() { return teams; }
    public void setTeams(String teams) { this.teams = teams; }
    @Override
    public String toString() {
        return "{\n" +
                "  \"userId\": \"" + userId + "\",\n" +
                "  \"projectName\": \"" + projectName + "\",\n" +
                "  \"projectDescription\": \"" + projectDescription + "\",\n" +
                "  \"projectDomain\": \"" + projectDomain + "\"\n" +
                "}";
    }
}
