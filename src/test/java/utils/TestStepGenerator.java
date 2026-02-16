package utils;

public class TestStepGenerator {

    public static String generateStepDescription(String target, String condition) {
        return String.format(
                "Verify %s %s",
                normalize(target),
                normalize(condition)
        );
    }

    public static String generateExpectedResult(String token) {
        return String.format(
                "Audit entry displays '%s' with current timestamp and sender information",
                token
        );
    }

    private static String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        input = input.trim();
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
}
