package models.connection;

public class TestConnectionRequest {

    private int userId;
    private int orgId;
    private Connection connection;

    public int getUserId() {
        return userId;
    }

    public int getOrgId() {
        return orgId;
    }

    public Connection getConnection() {
        return connection;
    }

    public static class Connection {

        private String id;
        private String type;
        private String endDate;
        private String orgName;
        private String siteUrl;
        private String email;
        private String spaceName;
        private String source;
        private String token;

        public String getId() { return id; }
        public String getType() { return type; }
        public String getEndDate() { return endDate; }
        public String getOrgName() { return orgName; }
        public String getSiteUrl() { return siteUrl; }
        public String getEmail() { return email; }
        public String getSpaceName() { return spaceName; }
        public String getSource() { return source; }
        public String getToken() { return token; }
    }
}
