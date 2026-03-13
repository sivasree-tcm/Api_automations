package tests.framework;

import api.framework.DownloadAtsFrameworkApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.JsonUtils;
import utils.ProjectStore;
import utils.TokenUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadAtsFrameworkTest extends BaseTest {

    public void downloadAtsFramework() {

        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();
        String projectName = ProjectStore.getProjectName(projectId);
        String automationFramework = ProjectStore.getAutomationFramework();
        String storageType = ProjectStore.getStorageType();

        if (projectName == null || automationFramework == null || storageType == null) {
            throw new RuntimeException("Missing framework configuration in ProjectStore");
        }

        System.out.println("Using Automation Framework -> " + automationFramework);
        System.out.println("Project Name -> " + projectName);
        System.out.println("Storage Type -> " + storageType);

        if (frameworkAlreadyExists(projectName, automationFramework)) {
            System.out.println("Skipping download - framework already exists for -> " + projectName);
            return;
        }

        deleteOldFrameworkFolders(automationFramework);

        Report testData = JsonUtils.readJson(
                "testdata/framework/downloadAtsFramework.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("downloadAtsFramework.json missing or invalid.");
        }

        for (Report.TestCase baseTc : testData.getTestCases()) {

            Report.TestCase tc = new Report.TestCase(baseTc);

            Map<String, Object> request = new HashMap<>();
            request.put("projectId", String.valueOf(projectId));
            request.put("userId", userId);
            request.put("automationFramework", automationFramework);
            request.put("projectName", projectName);
            request.put("storageType", storageType);

            tc.setRequest(request);
            tc.setTcId("DOWNLOAD_FRAMEWORK_" + projectId + "_" + automationFramework);
            tc.setName("Download ATS Framework | Project " + projectId + " | " + automationFramework);

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Response response = DownloadAtsFrameworkApi.downloadFramework(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("Framework API returned null response.");
                        }

                        if (response.getStatusCode() != tc.getExpectedStatusCode()) {
                            throw new RuntimeException(
                                    "Framework download failed -> " + automationFramework +
                                            " | Status -> " + response.getStatusCode()
                            );
                        }

                        System.out.println("Framework download successful -> " + automationFramework);
                        return response;
                    }
            );
        }
    }

    private boolean frameworkAlreadyExists(String projectName, String framework) {
        try {
            String folderName = projectName + "_" + framework;
            File baseDir = getBaseDirectory();
            File targetFolder = new File(baseDir, folderName);

            if (targetFolder.exists() && targetFolder.isDirectory()) {
                System.out.println("Framework folder already exists -> " + targetFolder.getAbsolutePath());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Could not check framework existence: " + e.getMessage());
        }
        return false;
    }

    private void deleteOldFrameworkFolders(String automationFramework) {
        try {
            File baseDir = getBaseDirectory();

            if (!baseDir.exists()) {
                System.out.println("Base directory does not exist yet, skipping cleanup.");
                return;
            }

            File[] oldFolders = baseDir.listFiles(f ->
                    f.isDirectory() && f.getName().contains(automationFramework)
            );

            if (oldFolders == null || oldFolders.length == 0) {
                System.out.println("No old framework folders found to delete.");
                return;
            }

            System.out.println("Found " + oldFolders.length + " old folder(s) to delete...");

            for (File folder : oldFolders) {
                boolean deleted = deleteFolder(folder);
                if (deleted) {
                    System.out.println("Deleted -> " + folder.getName());
                } else {
                    System.err.println("Could not fully delete -> " + folder.getName());
                }
            }

            System.out.println("Cleanup complete.");

        } catch (Exception e) {
            System.err.println("Cleanup warning: " + e.getMessage());
        }
    }

    private boolean deleteFolder(File folder) {
        if (folder == null || !folder.exists()) {
            return true;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    boolean deleted = f.delete();
                    if (!deleted) {
                        System.err.println("Could not delete file -> " + f.getAbsolutePath());
                    }
                }
            }
        }
        return folder.delete();
    }

    private File getBaseDirectory() {
        String basePath = System.getProperty("user.home")
                + File.separator
                + "AutomationFrameworkUnzipped";
        return new File(basePath);
    }
}
