package utils;

import java.util.ArrayList;
import java.util.List;

public class GeneratedTSStore {

    private static final List<Integer> GENERATED_TS = new ArrayList<>();

    /** Store TS IDs generated in current flow */
    public static void store(List<Integer> tsIds) {
        GENERATED_TS.clear();
        if (tsIds != null) {
            GENERATED_TS.addAll(tsIds);
        }
        System.out.println("📦 Stored TS IDs → " + GENERATED_TS);
    }

    /** Get all generated TS IDs */
    public static List<Integer> getAll() {
        return new ArrayList<>(GENERATED_TS);
    }

    /** Check if TS exists */
    public static boolean hasTS() {
        return !GENERATED_TS.isEmpty();
    }

    /** ✅ Get any one TS ID (used for TC generation, summary, etc.) */
    public static Integer getAnyTsId() {
        return GENERATED_TS.isEmpty() ? null : GENERATED_TS.get(0);
    }

    /** ✅ Get last TS ID (used for delete / update scenarios) */
    public static Integer getLastTsId() {
        return GENERATED_TS.isEmpty()
                ? null
                : GENERATED_TS.get(GENERATED_TS.size() - 1);
    }

    /** Clear store (optional, for cleanup between runs) */
    public static void clear() {
        GENERATED_TS.clear();
    }
}
