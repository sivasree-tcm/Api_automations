package utils;

import java.util.LinkedHashSet;
import java.util.Set;

public class TestStepStore {

    private static final Set<Integer> STEP_IDS = new LinkedHashSet<>();

    public static void add(Integer stepId) {
        if (stepId != null) {
            STEP_IDS.add(stepId);
        }
    }

    public static boolean hasSteps() {
        return !STEP_IDS.isEmpty();
    }

    /**
     * Returns ANY available StepId (deterministic order)
     */
    public static Integer getAnyStepId() {

        if (STEP_IDS.isEmpty()) {
            return null;
        }

        return STEP_IDS.iterator().next();
    }

    public static void clear() {
        STEP_IDS.clear();
    }

    public static void log() {
        System.out.println("📦 TestStepStore Contents → " + STEP_IDS);
    }
}