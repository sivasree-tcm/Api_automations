package tests.connection;

import java.util.List;

public class ConnectionReport {

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
        private String role;                  // ✅ ROLE SUPPORT
        private int expectedStatusCode;
        private Object request;               // ✅ GET API → Object / null

        public String getTcId() {
            return tcId;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public int getExpectedStatusCode() {
            return expectedStatusCode;
        }

        public Object getRequest() {
            return request;
        }

        @Override
        public String toString() {
            return tcId + " - " + name;
        }
    }
}