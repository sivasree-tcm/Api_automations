package tests.db;

import base.BaseTest;
import report.CustomReportManager;
import report.ReportContext;
import report.ReportStep;
import report.ReportTest;
import utils.ConfigReader;
import utils.ProjectStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DeleteProjectFromDB extends BaseTest {

    public static void deleteProjectFromDatabase() {

        String scenarioName = "Delete Project From DB";

        ReportTest test = new ReportTest(scenarioName);
        ReportContext.setTest(test);

        long start = System.currentTimeMillis();

        Connection connection = null;

        try {

            Integer projectId = ProjectStore.getSelectedProjectId();

            if (projectId == null) {
                ProjectStore.log();
                throw new RuntimeException("No ProjectId available in ProjectStore");
            }

            String url = ConfigReader.get("db.url");
            String username = ConfigReader.get("db.username");
            String password = ConfigReader.get("db.password");

            if (url == null || username == null || password == null) {
                throw new RuntimeException("Missing DB configuration in config.properties");
            }

            test.addStep(new ReportStep(
                    "Info",
                    "ProjectId Selected",
                    String.valueOf(projectId)
            ));

            String deleteAutomationFrameworks =
                    "delete from AutomationFrameworks where ProjectId=" + projectId;

            String deleteProjects =
                    "delete from projects where projectId=" + projectId;

            test.addStep(new ReportStep(
                    "Info",
                    "Executing Query",
                    deleteAutomationFrameworks
            ));

            test.addStep(new ReportStep(
                    "Info",
                    "Executing Query",
                    deleteProjects
            ));

            // ✅ CRITICAL FIX – Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement()) {

                int afRows = stmt.executeUpdate(deleteAutomationFrameworks);
                int projectRows = stmt.executeUpdate(deleteProjects);

                connection.commit();

                test.addStep(new ReportStep(
                        "Info",
                        "Rows Affected",
                        "AutomationFrameworks = " + afRows +
                                ", Projects = " + projectRows
                ));
            }

            test.markPassed(
                    "Project deleted successfully in " +
                            (System.currentTimeMillis() - start) + " ms"
            );

        } catch (Exception e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ignored) {}

            test.addStep(new ReportStep(
                    "Fail",
                    "Failure",
                    e.getMessage()
            ));

            test.markFailed("DB deletion failed");

        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignored) {}

            CustomReportManager.addTest(scenarioName, test);
        }
    }
}
