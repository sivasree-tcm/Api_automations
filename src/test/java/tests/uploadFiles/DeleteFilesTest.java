package tests.uploadFiles;

import api.uploadFiles.DeleteFilesApi;
import base.BaseTest;
import tests.connection.ConnectionReport;
import tests.user.ApiTestExecutor;
import utils.*;
import java.util.HashMap;
import java.util.Map;

public class DeleteFilesTest extends BaseTest {

    public void deleteSingleImageForBR() {
        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        // Get first BR ID
        Integer brId = BusinessRequirementStore.getBrIds(projectId).get(0);

        ConnectionReport testData = JsonUtils.readJson(
                "testdata/uploadFiles/uploadFilesForBR.json",
                ConnectionReport.class
        );

        // 🔥 FIX: Ensure the template case has a non-null Request Map before copying
        if (testData.getTestCases().get(0).getRequest() == null) {
            testData.getTestCases().get(0).setRequest(new HashMap<>());
        }

        // Now this call will not throw NullPointerException
        ConnectionReport.TestCase tc = new ConnectionReport.TestCase(testData.getTestCases().get(0));

        // Construct the dynamic Payload for deletion
        Map<String, Object> deletePayload = new HashMap<>();
        deletePayload.put("fileName", projectId + "/" + brId + "/sample.png");
        deletePayload.put("userProjectId", String.valueOf(projectId));
        deletePayload.put("userBrId", String.valueOf(brId));
        deletePayload.put("userId", String.valueOf(userId));
        deletePayload.put("storageType", "S3");

        tc.setRequest(deletePayload);
        tc.setTcId("DELETE_SINGLE_FILE_BR_" + brId);
        tc.setName("Delete Single Image | BR " + brId);

        ApiTestExecutor.execute(
                "Delete Single File API",
                tc,
                () -> DeleteFilesApi.deleteFile(deletePayload, tc.getRole())
        );
    }
}