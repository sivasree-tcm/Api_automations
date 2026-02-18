package db;

import base.BaseTest;
import utils.ConfigReader;
import utils.ProjectStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DeleteProjectFromDB extends BaseTest {

    public static void deleteProjectFromDatabase() {

        Integer projectId = ProjectStore.getSelectedProjectId();

        if (projectId == null) {
            ProjectStore.log();
            throw new RuntimeException(
                    "❌ No ProjectId available in ProjectStore. Cannot delete project."
            );
        }

        String url = ConfigReader.get("db.url");
        String username = ConfigReader.get("db.username");
        String password = ConfigReader.get("db.password");

        if (url == null || username == null || password == null) {
            throw new RuntimeException("❌ Missing DB configuration in config.properties");
        }

        System.out.println("🗑 Deleting ProjectId from DB → " + projectId);

        String deleteAutomationFrameworks =
                "delete from AutomationFrameworks where ProjectId=" + projectId;

        String deleteProjects =
                "delete from projects where projectId=" + projectId;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement stmt = connection.createStatement()) {

            connection.setAutoCommit(false);

            // ✅ Dependency-first deletion
            stmt.executeUpdate(deleteAutomationFrameworks);
            stmt.executeUpdate(deleteProjects);

            connection.commit();

            System.out.println("✅ Project deleted successfully from DB → " + projectId);

        } catch (Exception e) {
            throw new RuntimeException(
                    "❌ DB deletion failed for ProjectId → " + projectId, e
            );
        }
    }
}