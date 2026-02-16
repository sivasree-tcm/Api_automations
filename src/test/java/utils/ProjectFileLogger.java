package utils;

import java.io.*;

public class ProjectFileLogger {

    private static final String FILE_PATH = "reports/runtime/project_report.txt";

    public static synchronized void logSelectedProject() {

        Integer projectId;

        try {
            projectId = ProjectStore.getSelectedProjectId();
        } catch (Exception e) {
            System.out.println("ℹ No selected project available to log.");
            return;
        }

        if (projectId == null) return;

        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();

            // ✅ Deduplication Check
            if (file.exists()) {

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                    String line;
                    String entry = "ProjectId = " + projectId;

                    while ((line = br.readLine()) != null) {
                        if (entry.equals(line.trim())) {
                            System.out.println("ℹ ProjectId already logged → " + projectId);
                            return; // 🚀 Prevent duplicate
                        }
                    }
                }
            }

            // ✅ Append ONLY if not present
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                bw.write("ProjectId = " + projectId);
                bw.newLine();
            }

            System.out.println("✅ Logged ProjectId → " + projectId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to write project report file", e);
        }
    }


    public static File getReportFile() {
        return new File(FILE_PATH);
    }

    public static synchronized void clearFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) file.delete();
    }
}
