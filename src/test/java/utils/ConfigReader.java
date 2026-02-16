package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input =
                    ConfigReader.class.getClassLoader()
                            .getResourceAsStream("config.properties");

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties");
        }
    }

    public static String get(String key) {

        if ("base.url".equals(key)) {
            return resolveBaseUrl();
        }

        return properties.getProperty(key);
    }

    private static String resolveBaseUrl() {

        String instance = properties.getProperty("instance");

        if (instance == null) {
            throw new RuntimeException("❌ instance not defined in config.properties");
        }

        switch (instance.toLowerCase()) {

            case "dev-tsigma":
                return "https://dev.tsigma.ai";

            case "dev-playwright":
                return "https://devpw.tsigma.ai";

            case "test-tsigma":
                return "https://test.tsigma.ai";

            case "test-playwright":
                return "https://testpw.tsigma.ai";

            case "test-kb":
                return "https://testkb.tsigma.ai";

            default:
                throw new RuntimeException("❌ Invalid instance value → " + instance);
        }
    }
}
