package tests.files;

import api.files.ListFilesApi;
import base.BaseTest;
import io.restassured.response.Response;
import report.ApiTestExecutor;
import report.Report;
import utils.*;

import java.util.HashMap;
import java.util.Map;

public class ListFilesTest extends BaseTest {

    public void listFiles() {

        Integer userId = TokenUtil.getUserId();

        if (userId == null) {
            throw new RuntimeException("❌ UserId missing from TokenUtil.");
        }

        Report testData = JsonUtils.readJson(
                "testdata/uploadFiles/listFiles.json",
                Report.class
        );

        if (testData == null || testData.getTestCases() == null) {
            throw new RuntimeException("❌ listFiles.json missing or invalid.");
        }

        for (Report.TestCase tc : testData.getTestCases()) {

            ApiTestExecutor.execute(
                    testData.getScenario(),
                    tc,
                    () -> {

                        Map<String, Object> request = (tc.getRequest() != null)
                                ? new HashMap<>((Map<String, Object>) tc.getRequest())
                                : new HashMap<>();


                        /* ==============================
                           Inject Dynamic Project ID
                           ============================== */

                        Object projectVal = request.get("userProjectId");

                        if ("DYNAMIC_PROJECT".equals(projectVal)) {

                            Integer projectId = ProjectStore.getSelectedProjectId();

                            if (projectId == null) {
                                projectId = ProjectStore.getAnyProjectId();
                            }

                            if (projectId == null) {
                                throw new RuntimeException("❌ No Project ID found in ProjectStore.");
                            }

                            request.put("userProjectId", projectId.toString());

                            System.out.println("🔗 Injected Project ID → " + projectId);
                        }


                        /* ==============================
                           Inject Dynamic BR ID
                           ============================== */

                        Object brVal = request.get("userBrId");

                        if ("DYNAMIC_BR".equals(brVal)) {

                            Integer projectId = ProjectStore.getSelectedProjectId();

                            Integer brId = BusinessRequirementStore.getAnyBrId(projectId);

                            if (brId == null) {
                                throw new RuntimeException("❌ No BR ID found in BusinessRequirementStore.");
                            }

                            request.put("userBrId", brId);

                            System.out.println("🔗 Injected BR ID → " + brId);
                        }


                        /* ==============================
                           Inject Dynamic User ID
                           ============================== */

                        Object userVal = request.get("userId");

                        if ("DYNAMIC_USER".equals(userVal)) {

                            request.put("userId", userId.toString());

                            System.out.println("🔗 Injected User ID → " + userId);
                        }


                        /* ==============================
                           Inject Dynamic Storage Type
                           ============================== */

                        Object storageVal = request.get("storageType");

                        if ("DYNAMIC_STORAGE".equals(storageVal)) {

                            String storageType = ProjectStore.getStorageType();

                            if (storageType == null) {
                                throw new RuntimeException("❌ StorageType missing in ProjectStore.");
                            }

                            request.put("storageType", storageType);

                            System.out.println("🔗 Injected Storage Type → " + storageType);
                        }


                        /* Update request in report */
                        tc.setRequest(request);

                        System.out.println("📦 Final ListFiles Payload → " + request);


                        Response response = ListFilesApi.listFiles(
                                request,
                                tc.getRole(),
                                tc.getAuthType()
                        );

                        if (response == null) {
                            throw new RuntimeException("❌ API returned NULL response.");
                        }

                        return response;
                    }
            );
        }
    }
}