package utils;

public class PromptStore {

    private static Integer promptId;

    public static void setPromptId(Integer id) {
        promptId = id;
    }

    public static Integer getPromptId() {
        return promptId;
    }

    public static boolean hasPromptId() {
        return promptId != null;
    }

    public static void clear() {
        promptId = null;
    }
}
