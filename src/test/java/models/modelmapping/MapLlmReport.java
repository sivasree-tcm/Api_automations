package models.modelmapping;

import lombok.Data;
import java.util.List;

@Data
public class MapLlmReport {

    private String scenario;
    private List<TestCase> testCases;

    public static class TestCase {

        private String name;
        private String role;
        private String authType;
        private String modelType; // ✅ ADD THIS
        private Object request;
        private int expectedStatus;

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public String getAuthType() {
            return authType;
        }

        public String getModelType() {
            return modelType;
        }

        public Object getRequest() {
            return request;
        }

        public int getExpectedStatus() {
            return expectedStatus;
        }
    }

}
