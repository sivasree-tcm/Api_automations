package report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

    private String scenario;
    private List<TestCase> testCases;
    // ✅ Add this field
    private String modelType;


    // ✅ REQUIRED for Jackson
    public Report() {
    }

    public String getScenario() {
        return scenario;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }


    public static class TestCase {
        // ✅ REQUIRED
        public TestCase() {}

        private String tcId;
        private String name;
        private String role;                  // ✅ ROLE SUPPORT
        private int expectedStatusCode;
        private Object request;               // ✅ GET API → Object / null
        private String authType;
        private String modelType;

        public TestCase(TestCase other) {
            this.tcId = other.tcId;
            this.name = other.name;
            this.role = other.role;
            this.authType = other.authType;
            this.expectedStatusCode = other.expectedStatusCode;
            this.request = new HashMap<>((Map<String, Object>) other.request);
        }

        // ✅ Getter for modelType
        public String getModelType() {
            return modelType;
        }

        // ✅ Setter for modelType
        public void setModelType(String modelType) {
            this.modelType = modelType;
        }

        // ✅ REQUIRED SETTERS
        public void setTcId(String tcId) {
            this.tcId = tcId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRequest(Object request) {
            this.request = request;
        }

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

        public String getAuthType() {
            return authType;
        }

        public void setExpectedStatusCode(int expectedStatusCode) {
            this.expectedStatusCode = expectedStatusCode;
        }


        @Override
        public String toString() {
            return tcId + " - " + name;
        }
    }


}