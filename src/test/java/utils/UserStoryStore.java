package utils;

import java.util.ArrayList;
import java.util.List;

public class UserStoryStore {

    private static final List<Integer> USER_STORY_IDS = new ArrayList<>();

    public static void setUserStoryIds(List<Integer> ids) {
        USER_STORY_IDS.clear();
        USER_STORY_IDS.addAll(ids);
    }

    public static List<Integer> getUserStoryIds() {
        return USER_STORY_IDS;
    }

    public static boolean hasStories() {
        return !USER_STORY_IDS.isEmpty();
    }

    public static void clear() {
        USER_STORY_IDS.clear();
    }
}
