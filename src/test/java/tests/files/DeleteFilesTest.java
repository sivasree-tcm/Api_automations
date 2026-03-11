package tests.files;

import api.files.DeleteFilesApi;
import base.BaseTest;
import report.Report;
import report.ApiTestExecutor;
import utils.*;
import java.util.HashMap;
import java.util.Map;

public class DeleteFilesTest extends BaseTest {

    public void deleteSingleImageForBR() {
        Integer projectId = ProjectStore.getSelectedProjectId();
        Integer userId = TokenUtil.getUserId();

        // Get first BR ID
        Integer brId = BusinessRequirementStore.getBrIds(projectId).get(0);

        Report testData = JsonUtils.readJson(
                "testdata/uploadFiles/deleteFiles.json",
                Report.class
        );

        // 🔥 FIX: Ensure the template case has a non-null Request Map before copying
        if (testData.getTestCases().get(0).getRequest() == null) {
            testData.getTestCases().get(0).setRequest(new HashMap<>());
        }

        // Now this call will not throw NullPointerException
        Report.TestCase tc = new Report.TestCase(testData.getTestCases().get(0));

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