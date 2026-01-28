package models.queue;

public class GetGenerationQueueRequest {

    private Integer userIdFromAPI;
    private Integer projectId;
    private String userId;
    private String userName;
    private String projectName;

    // ✅ No-arg constructor (required for Jackson)
    public GetGenerationQueueRequest() {
    }

    // ✅ All-args constructor (optional but useful)
    public GetGenerationQueueRequest(
            Integer userIdFromAPI,
            Integer projectId,
            String userId,
            String userName,
            String projectName
    ) {
        this.userIdFromAPI = userIdFromAPI;
        this.projectId = projectId;
        this.userId = userId;
        this.userName = userName;
        this.projectName = projectName;
    }

    // ---------- Getters & Setters ----------

    public Integer getUserIdFromAPI() {
        return userIdFromAPI;
    }

    public void setUserIdFromAPI(Integer userIdFromAPI) {
        this.userIdFromAPI = userIdFromAPI;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "GetGenerationQueueRequest{" +
                "userIdFromAPI=" + userIdFromAPI +
                ", projectId=" + projectId +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
