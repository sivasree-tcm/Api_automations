package tests.user;

import models.user.RegisterUserRequest;
import java.util.List;

public class RegisterUserTestData {

    private String scenario;
    private List<TestCase> positiveTestCases;
    private List<TestCase> negativeTestCases;

    public String getScenario() {
        return scenario;
    }

    public List<TestCase> getPositiveTestCases() {
        return positiveTestCases;
    }

    public List<TestCase> getNegativeTestCases() {
        return negativeTestCases;
    }

    // ================= INNER CLASS =================
    public static class TestCase {

        private String name;

        // ✅ NEW
        private String method;
        private String endpoint;

        private int expectedStatusCode;
        private String expectedStatus;
        private RegisterUserRequest request;

        public String getName() {
            return name;
        }

        public String getMethod() {
            return method;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public int getExpectedStatusCode() {
            return expectedStatusCode;
        }

        public String getExpectedStatus() {
            return expectedStatus;
        }

        public RegisterUserRequest getRequest() {
            return request;
        }
    }
}
