package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ✅ Read JSON from classpath
    public static <T> T readJson(String classpathLocation, Class<T> clazz) {
        try {
            InputStream is = JsonUtils.class
                    .getClassLoader()
                    .getResourceAsStream(classpathLocation);

            if (is == null) {
                throw new RuntimeException(
                        "JSON file not found in classpath: " + classpathLocation
                );
            }

            return mapper.readValue(is, clazz);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read JSON file: " + classpathLocation, e
            );
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
