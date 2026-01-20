package utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ✅ Read JSON from classpath
    public static <T> T readJson(String path, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                throw new RuntimeException("JSON file not found: " + path);
            }

            return mapper.readValue(is, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON: " + path, e);
        }
    }
    public static Map<String, Object> readJsonAsMap(String path) {
        try {
            InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path);

            if (is == null) {
                throw new RuntimeException("JSON file not found: " + path);
            }

            return mapper.readValue(is, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON: " + path, e);
        }
    }


    // ✅ POJO → JSON (for Allure)
    public static String toJson(Object obj) {
        try {
            return mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert object to JSON", e);
        }
    }
}