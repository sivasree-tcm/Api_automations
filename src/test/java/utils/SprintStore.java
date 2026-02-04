package utils;

import java.util.List;

public class SprintStore {

    private static List<String> sprints;
    private static String selectedSprint;

    public static void setSprints(List<String> sprintList) {
        sprints = sprintList;
    }

    public static List<String> getSprints() {
        return sprints;
    }

    public static void setSelectedSprint(String sprint) {
        selectedSprint = sprint;
    }

    public static String getSelectedSprint() {
        return selectedSprint;
    }

    public static void clear() {
        sprints = null;
        selectedSprint = null;
    }
}