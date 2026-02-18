package models.connection;

import java.util.List;

public class DbConfigReport {
    private String scenario;
    private List<TestCase> testCases; // ✅ Matches "testCases" array in JSON

    // Getters and Setters
    public String getScenario() { return scenario; }
    public List<TestCase> getTestCases() { return testCases; }

    public static class TestCase {
        private String tcId;
        private String name;
        private String role;
        private String authType;
        private int expectedStatusCode;
        private Object request; // Maps to the Map<String, Object> in your test

        // Getters and Setters for all fields above
        public String getTcId() { return tcId; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getAuthType() { return authType; }
        public int getExpectedStatusCode() { return expectedStatusCode; }
        public Object getRequest() { return request; }
    }
}