package utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class TestCaseStore {

    private static final Set<Integer> TEST_CASE_IDS = new LinkedHashSet<>();
    private static final Set<String> TEST_CASE_NUMBERS = new LinkedHashSet<>();

    // ✅ Add TC
    public static void add(Integer tcId) {
        if (tcId != null) {
            TEST_CASE_IDS.add(tcId);
        }
    }

    // ✅ NEW
    public static void addTestCaseNumber(String tcNumber) {
        if (tcNumber != null && !tcNumber.isBlank()) {
            TEST_CASE_NUMBERS.add(tcNumber);
        }
    }

    // ✅ NEW
    public static String getAnyTestCaseNumber() {
        return TEST_CASE_NUMBERS.stream().findFirst().orElse(null);
    }

    // ✅ Used by Update TC / Get TC Steps
    public static Integer getAnyTestCaseId() {
        return TEST_CASE_IDS.stream().findFirst().orElse(null);
    }

    // ✅ REQUIRED for iteration & validation
    public static List<Integer> getAll() {
        return new ArrayList<>(TEST_CASE_IDS);
    }

    // ✅ Guard checks
    public static boolean hasTestCases() {
        return !TEST_CASE_IDS.isEmpty();
    }

    // ✅ Debug-friendly logging
    public static void log() {
        System.out.println("📦 Stored TestCase IDs → " + TEST_CASE_IDS);
    }

    // ✅ Reset between runs
    public static void clear() {
        TEST_CASE_IDS.clear();
    }
}
