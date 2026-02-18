package utils;

import java.io.File;

public class DailyReportJob {

    public static void main(String[] args) {

        File reportFile = ProjectFileLogger.getReportFile();

        if (!reportFile.exists()) {
            System.out.println("ℹ No projects executed today.");
            return;
        }

        System.out.println("📧 Sending Daily Project Execution Report...");

        EmailSender.sendReport(reportFile);

        ProjectFileLogger.clearFile();

        System.out.println("✅ Report file cleared for next day.");
    }
}