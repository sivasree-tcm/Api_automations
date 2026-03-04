package utils;

import java.util.ArrayList;
import java.util.List;

public class LogStore {

    private static final List<String> logFiles = new ArrayList<>();

    public static void addLogFile(String fileName) {

        if (fileName != null && !fileName.isBlank()) {
            logFiles.add(fileName);
        }
    }

    public static List<String> getLogFiles() {

        if (logFiles.isEmpty()) {
            throw new RuntimeException("❌ No log files stored in LogStore");
        }

        return new ArrayList<>(logFiles);
    }

    public static void clear() {
        logFiles.clear();
    }
}