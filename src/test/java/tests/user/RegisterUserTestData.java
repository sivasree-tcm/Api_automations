package tests.user;

import models.user.RegisterUserRequest;

import java.util.List;

public class RegisterUserTestData {

    private List<TestCase> positiveTestCases;
    private List<TestCase> negativeTestCases;

    public List<TestCase> getPositiveTestCases() {
        return positiveTestCases;
    }

    public List<TestCase> getNegativeTestCases() {
        return negativeTestCases;
    }

    public static class TestCase {

        private String name;
        private int expectedStatusCode;
        private String expectedStatus;
        private RegisterUserRequest request;

        public String getName() {
            return name;
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
