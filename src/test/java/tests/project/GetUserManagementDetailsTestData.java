package tests.project;


import java.util.List;

public class GetUserManagementDetailsTestData {

    private String scenario;
    private List<TestCase> testCases;

    public String getScenario() {
        return scenario;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public static class TestCase {

        private String tcId;
        private String name;
        private String role;
        private int expectedStatusCode;
        private Object request;
        private String expectedMessage;
        private Integer maxResponseTimeMs;

        public String getTcId() { return tcId; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public int getExpectedStatusCode() { return expectedStatusCode; }
        public Object getRequest() { return request; }
        public String getExpectedMessage() { return expectedMessage; }
        public Integer getMaxResponseTimeMs() { return maxResponseTimeMs; }

        @Override
        public String toString() {
            return tcId + " - " + name;
        }
    }
}

