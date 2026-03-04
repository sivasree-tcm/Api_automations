package tests.logs;

import base.BaseTest;
import org.testng.annotations.Test;

public class LogFlowTest extends BaseTest {

    @Test
    public void step1_getLogFiles() {

        System.out.println("▶ Step 1: Fetching Log Files");

        GetLogFilesTest getLogs = new GetLogFilesTest();
        getLogs.getLogFilesApiTest();
    }

    @Test(dependsOnMethods = "step1_getLogFiles")
    public void step2_downloadLogFile() {

        System.out.println("▶ Step 2: Downloading Log File");

        DownloadLogFileTest downloadLogs = new DownloadLogFileTest();
        downloadLogs.downloadLogFileApiTest();
    }
}