package models.connection;

public class DeleteConnectionRequest {

    private String userId;
    private String orgId;
    private String connectionId;

    public String getUserId() {
        return userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getConnectionId() {
        return connectionId;
    }
}
