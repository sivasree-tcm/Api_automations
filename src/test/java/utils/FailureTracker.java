package utils;

import java.util.ArrayList;
import java.util.List;

public class FailureTracker {

    private static final List<String> failedCases = new ArrayList<>();

    public static void addFailure(String message) {
        failedCases.add(message);
    }

    public static List<String> getFailures() {
        return failedCases;
    }

    public static boolean hasFailures() {
        return !failedCases.isEmpty();
    }

    public static void clear() {
        failedCases.clear();
    }
}
