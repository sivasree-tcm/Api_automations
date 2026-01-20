package tests.project;

import models.project.ProjectRequest;
import java.util.List;

public class ProjectTestData {

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
        private int expectedStatusCode;
        private ProjectRequest request;

        public String getTcId() {
            return tcId;
        }

        public String getName() {
            return name;
        }

        public int getExpectedStatusCode() {
            return expectedStatusCode;
        }

        public ProjectRequest getRequest() {
            return request;
        }

        @Override
        public String toString() {
            return tcId + " - " + name;
        }
    }
}
