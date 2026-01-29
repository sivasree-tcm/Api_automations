package tests.prompt;

import java.util.List;
import java.util.Map;

public class CreatePromptTestData {

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
        private Map<String, Object> request;

        public String getTcId() { return tcId; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public int getExpectedStatusCode() { return expectedStatusCode; }
        public Map<String, Object> getRequest() { return request; }
        @Override
        public String toString() {
            return tcId + " | " + name;
        }
    }
}
