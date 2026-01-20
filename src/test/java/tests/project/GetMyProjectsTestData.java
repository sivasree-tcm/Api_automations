package tests.project;

import java.util.List;

public class GetMyProjectsTestData {

    private String scenario;
    private List<TestCase> testCases;

    public String getScenario() {
        return scenario;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public static class TestCase {

        private String name;
        private int expectedStatusCode;
        private Integer minSize;
        private String listPath;
        private String requiredField;

        public String getName() {
            return name;
        }

        public int getExpectedStatusCode() {
            return expectedStatusCode;
        }

        public Integer getMinSize() {
            return minSize;
        }

        public String getListPath() {
            return listPath;
        }

        public String getRequiredField() {
            return requiredField;
        }
    }
}
