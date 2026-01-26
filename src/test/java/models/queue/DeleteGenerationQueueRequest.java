package models.queue;

import java.util.List;

public class DeleteGenerationQueueRequest {

    private Integer userIdFromAPI;
    private String userId;
    private String projectId;
    private List<Integer> queueIds;
    private String userName;
    private String projectName;

    public Integer getUserIdFromAPI() {
        return userIdFromAPI;
    }

    public void setUserIdFromAPI(Integer userIdFromAPI) {
        this.userIdFromAPI = userIdFromAPI;
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

    public List<Integer> getQueueIds() {
        return queueIds;
    }

    public void setQueueIds(List<Integer> queueIds) {
        this.queueIds = queueIds;
    }

    public String getUserName() {
        return userName;
    }

    public String getProjectName() {
        return projectName;
    }
}
