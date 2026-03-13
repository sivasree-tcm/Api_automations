package tests.flow;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.br.DeleteBusinessRequirementTest;
import tests.br.GetBusinessRequirementTest;
import tests.configuration.EnvironmentDetailsTest;
import tests.configuration.GetConfigTest;
import tests.configuration.SaveConfigTest;
import tests.connection.DeleteConnectionTest;
import tests.connection.SaveConnectionTest;
import tests.files.ListFilesTest;
import tests.generation.GenerateTSTest;
import tests.login.LogOutTest;
import tests.modelmapping.GetLlmCredentialsTest;
import tests.modelmapping.MapCredentialToProjectTest;
import tests.pipeline.ExecuteTestsTest;
import tests.prompt.GetAllPromptTest;
import tests.roles.*;
import tests.testCase.*;
import tests.testScenario.AddTestScenarioTest;
import tests.testScenario.DeleteTestScenarioTest;
import tests.testScenario.GetTSByBRTest;
import tests.testScenario.UpdateTestScenarioTest;
import tests.userManagement.ToggleUserStatusTest;
import tests.video.GetAutomationVideoTest;
import tests.ats.LoadATSFilesTest;
import tests.bdd.UpdateBddTest;
import tests.br.CheckGcsPathForBrTest;
import tests.configuration.DbConfigTest;
import tests.connection.GetConnectionsTest;
import tests.export.ExportTCExcelTest;
import tests.framework.DownloadAtsFrameworkTest;
import tests.generation.*;
import tests.generation.GetGenerationStatusTest;
import tests.logs.DownloadLogFileTest;
import tests.logs.GetLogFilesTest;
import tests.organization.GetOrganizationsTest;
import tests.project.*;
import tests.modelmapping.GetLlmModelsTest;
import tests.modelmapping.MapLlmToProjectTest;
import tests.br.UploadBusinessRequirementTest;
import tests.prompt.UpdatePromptTest;
import tests.prompt.CreatePromptTest;
import tests.prompt.MapPromptTest;
import tests.azure.GetAzureSprintsTest;
import tests.azure.GetUserStoriesTest;
import tests.azure.ImportAzureUserStoriesTest;
import tests.system.GetVersionTest;
import tests.files.DeleteFilesTest;
import tests.files.DownloadFilesTest;
import tests.files.UploadFilesForBRTest;
import tests.userManagement.AddUpdateProjecTest;
import tests.user.RegisterUserTest;
import tests.userManagement.GetUserManagementDetailsTest;
import tests.video.ListVideosTest;
import utils.*;

public class EndToEndFlow extends BaseTest {

    @Test
    public void step1_getOrg() {
        System.out.println("▶ Step 1: Get Organizations");
        new GetOrganizationsTest().getOrganizationsApiTest();
    }

    @Test(dependsOnMethods = "step1_getOrg")
    public void step2_registerUser() {
        System.out.println("▶ Step 2: Register User");
        new RegisterUserTest().registerUserTest();
    }

    @Test(dependsOnMethods = "step2_registerUser")
    public void step3_saveConnection(){
        System.out.println("▶ Step 3: Save Connection");
        new SaveConnectionTest().saveConnectionTest();
    }

    @Test(dependsOnMethods = "step3_saveConnection")
    public void step4_getConnections() {
        System.out.println("▶ Step 4: Get Connections");
        new GetConnectionsTest().getConnectionsApiTest();
    }

    @Test(dependsOnMethods = "step4_getConnections")
    public void step5_deleteConnection(){
        System.out.println("▶ Step 5: Delete Connection");
        new DeleteConnectionTest().deleteConnectionTest();
    }

    @Test(dependsOnMethods = "step5_deleteConnection")
    public void step6_listVideos() {
        System.out.println("▶ Step 6: List Videos");
        new ListVideosTest().listVideosApiTest();
    }

    @Test(dependsOnMethods = "step6_listVideos")
    public void step7_checkUserRoles() {
        System.out.println("▶ Step 7: Check User Roles");
        new CheckUserRolesTest().checkUserRoles();
    }

    @Test(dependsOnMethods = "step7_checkUserRoles")
    public void step8_getMyProjects() {
        System.out.println("▶ Step 8: Get My Projects");
        new GetMyProjectsTest().getMyProjectsApiTest();
    }

    @Test(dependsOnMethods = "step8_getMyProjects")
    public void step9_createProject() {
        System.out.println("▶ Step 9: Create Project");
        new CreateProjectTest().projectApiTest();
    }

    @Test(dependsOnMethods = "step9_createProject")
    public void step10_editProject() {
        System.out.println("▶ Step 10: Edit Project");
        new EditProjectTest().editProjectApiTest();
    }

    @Test(dependsOnMethods = "step10_editProject")
    public void step11_AddDbConfig() {
        System.out.println("▶ Step 11: Add DB Config");
        new DbConfigTest().addDbInfoTest();
    }

    @Test(dependsOnMethods = "step11_AddDbConfig")
    public void step12_getProjectDetails() {
        System.out.println("▶ Step 12: Get Project Details");
        new GetProjectDetailsTest().fetchProjectDetails(ProjectStore.getProjectId());
    }

    @Test(dependsOnMethods = "step12_getProjectDetails")
    public void step13_getModels() {
        System.out.println("▶ Step 13: Get LLM Models");
        new GetLlmModelsTest().fetchAndStoreModels();
    }

    @Test(dependsOnMethods = "step13_getModels")
    public void step14_mapModels() {
        System.out.println("▶ Step 14: Map Models");
        new MapLlmToProjectTest().mapLlmToProjectTest();
    }

    @Test(dependsOnMethods = "step14_mapModels")
    public void step15_getLlmCredentials() {
        System.out.println("▶ Step 15: Get LLM Credentials");
        new GetLlmCredentialsTest().fetchLlmCredentials();
    }

    @Test(dependsOnMethods = "step15_getLlmCredentials")
    public void step16_mapCredentialToProject() {
        System.out.println("▶ Step 16: Map Credential To Project");
        new MapCredentialToProjectTest().mapCredentialToProjectApiTest();
    }

    @Test(dependsOnMethods = "step16_mapCredentialToProject")
    public void step17_getProviderConfig() {
        System.out.println("▶ Step 17: Get Provider Config");
        new GetProjectProviderConfigTest().getProjectProviderConfigApiTest();
    }

    @Test(dependsOnMethods = "step17_getProviderConfig")
    public void step18_getEnvironmentDetails() {
        System.out.println("▶ Step 18: Get Environment Details");
        new EnvironmentDetailsTest().fetchEnvironmentDetailsTest();
    }

    @Test(dependsOnMethods = "step18_getEnvironmentDetails")
    public void step19_saveProjectConfig() {
        System.out.println("▶ Step 19: Save Project Config");
        new SaveConfigTest().saveConfigTest();
    }

    @Test(dependsOnMethods = "step19_saveProjectConfig")
    public void step20_getConfiguration() {
        System.out.println("▶ Step 20: Get Configuration");
        new GetConfigTest().getConfigApiTest();
    }

    @Test(dependsOnMethods = "step20_getConfiguration")
    public void step21_createRole() {
        System.out.println("▶ Step 21: Create Role");
        new CreateRoleTest().createRoleTest();
    }

    @Test(dependsOnMethods = "step21_createRole")
    public void step22_addUser() {
        System.out.println("▶ Step 22: Add User");
        new AddUpdateProjecTest().addUpdateProjectUserApiTest();
    }

    @Test(dependsOnMethods = "step22_addUser")
    public void step23_getRoles() {
        System.out.println("▶ Step 23: Get Roles");
        new GetRolesTest().getRolesForProject();
    }

    @Test(dependsOnMethods = "step23_getRoles")
    public void step24_editRoles() {
        System.out.println("▶ Step 24: Edit Roles");
        new EditRoleTest().editRoles();
    }

    @Test(dependsOnMethods = "step24_editRoles")
    public void step25_toggleUser() {
        System.out.println("▶ Step 25: Toggle User Status");
        new ToggleUserStatusTest().toggleUserStatusApiTest();
    }


    @Test(dependsOnMethods = "step25_toggleUser")
    public void step26_getUserManagementDetails() {
        System.out.println("▶ Step 26: Get User Management Details");
        new GetUserManagementDetailsTest().getUserManagementDetails();
    }

    @Test(dependsOnMethods = "step26_getUserManagementDetails")
    public void step27_disableUserByRole() {
        System.out.println("▶ Step 27: Disable Users By Role");
        new DisableUsersByRoleTest().disableUsersByRole();
    }

    @Test(dependsOnMethods = "step27_disableUserByRole")
    public void step28_DeleteRole() {
        System.out.println("▶ Step 28: Delete Role");
        new DeleteRoleTest().deleteRoles();
    }

    @Test(dependsOnMethods = "step28_DeleteRole")
    public void step29_createPrompt() {
        System.out.println("▶ Step 29: Create Prompt");
        new CreatePromptTest().createPromptApiTest();
    }

    @Test(dependsOnMethods = "step29_createPrompt")
    public void step30_getPrompts() {
        System.out.println("▶ Step 30: Get All Prompts");
        new GetAllPromptTest().getPromptApiTest();
    }

    @Test(dependsOnMethods = "step29_createPrompt")
    public void step30_updatePrompt() {
        System.out.println("▶ Step 30: Update Prompt");
        new UpdatePromptTest().updatePromptApiTest();
    }

    @Test(dependsOnMethods = "step30_updatePrompt")
    public void step31_mapPrompt() {
        System.out.println("▶ Step 31: Map Prompt");
        new MapPromptTest().mapPromptApiTest();
    }

    @Test(dependsOnMethods = "step31_mapPrompt")
    public void step32_getAzureSprints() {
        System.out.println("▶ Step 32: Get Azure Sprints");
        new GetAzureSprintsTest().getAzureDevOpsSprints();
    }

    @Test(dependsOnMethods = "step32_getAzureSprints")
    public void step33_getUserStories() {
        System.out.println("▶ Step 33: Get User Stories");
        new GetUserStoriesTest().getUserStories();
    }

    @Test(dependsOnMethods = "step33_getUserStories")
    public void step34_importAzureUserStories() {
        System.out.println("▶ Step 34: Import Azure User Stories");
        new ImportAzureUserStoriesTest().importAzureUserStories();
    }

    @Test(dependsOnMethods = "step34_importAzureUserStories")
    public void step35_uploadBusinessRequirement() {
        System.out.println("▶ Step 35: Upload Business Requirement");
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step35_uploadBusinessRequirement")
    public void step36_exportBRExcel() throws Exception {
        System.out.println("▶ Step 36: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step36_exportBRExcel")
    public void step37_deleteBR() throws Exception {
        System.out.println("▶ Step 37: Delete Business Requirement");
        new DeleteBusinessRequirementTest().deleteLastBR();
    }

    @Test(dependsOnMethods = "step37_deleteBR")
    public void step38_refreshBRs() {
        System.out.println("▶ Step 38: Refresh Business Requirements");
        BusinessRequirementStore.clear();
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step38_refreshBRs")
    public void step39_checkGCSForBR() {
        System.out.println("▶ Step 39: Check GCS Path For BR");
        new CheckGcsPathForBrTest().checkGcsPathForBrApiTest();
    }

    @Test(dependsOnMethods = "step39_checkGCSForBR")
    public void step40_importImageForBR() {
        System.out.println("▶ Step 40: Upload Image For BR");
        new UploadFilesForBRTest().uploadImageForALLBRs();
    }

    @Test(dependsOnMethods = "step40_importImageForBR")
    public void step41_listFiles() {
        System.out.println("▶ Step 41: List Files");
        new ListFilesTest().listFiles();
    }

    @Test(dependsOnMethods = "step41_listFiles")
    public void step42_downloadImage() {
        System.out.println("▶ Step 42: Download Image");
        new DownloadFilesTest().downloadSingleImageForBR();
    }

    @Test(dependsOnMethods = "step42_downloadImage")
    public void step43_deleteImage() {
        System.out.println("▶ Step 43: Delete Image");
        new DeleteFilesTest().deleteSingleImageForBR();
    }

    @Test(dependsOnMethods = "step43_deleteImage")
    public void step44_generateTS() {
        System.out.println("▶ Step 44: Generate Test Scenarios");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step44_generateTS")
    public void step45_waitForTSGeneration() {
        System.out.println("▶ Step 45: Wait For TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step45_waitForTSGeneration")
    public void step46_getTSByBR() {
        System.out.println("▶ Step 46: Get Test Scenarios By BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step46_getTSByBR")
    public void step47_exportTSExcel() throws Exception {
        System.out.println("▶ Step 47: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step47_exportTSExcel")
    public void step48_deleteTestScenario() {
        System.out.println("▶ Step 48: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step48_deleteTestScenario")
    public void step49_addTestScenario() {
        System.out.println("▶ Step 49: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step49_addTestScenario")
    public void step50_updateTestScenario() {
        System.out.println("▶ Step 50: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step50_updateTestScenario")
    public void step51_generateTC() {
        System.out.println("▶ Step 51: Generate Test Cases");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step51_generateTC")
    public void step52_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 52: Wait For TC Completion");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step52_waitForTCCompletion")
    public void step53_exportTC() {
        System.out.println("▶ Step 53: Export Test Cases");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step53_exportTC")
    public void step54_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 54: Get Test Case Summary For TS");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step54_getTestCaseSummaryForTS")
    public void step55_getTestCaseWithSteps() {
        System.out.println("▶ Step 55: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step55_getTestCaseWithSteps")
    public void step56_addTestCaseStep() {
        System.out.println("▶ Step 56: Add Test Case Step");
        new AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step56_addTestCaseStep")
    public void step57_updateTestCaseStepOrder() {
        System.out.println("▶ Step 57: Update Test Case Step Order");
        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
    }

    @Test(dependsOnMethods = "step57_updateTestCaseStepOrder")
    public void step58_updateTestCaseStep() {
        System.out.println("▶ Step 58: Update Test Case Step");
        new UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step58_updateTestCaseStep")
    public void step59_deleteTestCaseStep() {
        System.out.println("▶ Step 59: Delete Test Case Step");
        new DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step59_deleteTestCaseStep")
    public void step60_updateTestCase() {
        System.out.println("▶ Step 60: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step60_updateTestCase")
    public void step61_addTestCase() {
        System.out.println("▶ Step 61: Add Test Case");
        new InsertTestCaseTest().addTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step61_addTestCase")
    public void step62_updateBDD() {
        System.out.println("▶ Step 62: Update BDD");
        new UpdateBddTest().updateBdd();
    }

    @Test(dependsOnMethods = "step57_updateTestCaseStepOrder")
    public void step64_deleteTestCase() {
        System.out.println("▶ Step 64: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step64_deleteTestCase")
    public void step65_generateAutomationCode() {
        System.out.println("▶ Step 65: Generate Automation Code (ATS)");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step65_generateAutomationCode")
    public void step66_checkATSStatus() {
        System.out.println("▶ Step 66: Check ATS Generation Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step66_checkATSStatus")
    public void step67_downloadATSFramework() {
        System.out.println("▶ Step 67: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
    }

    @Test(dependsOnMethods = "step67_downloadATSFramework")
    public void step68_loadATSFiles() {
        System.out.println("▶ Step 68: Load ATS Files");
        new LoadATSFilesTest().loadATSFiles();
    }

    @Test(dependsOnMethods = "step68_loadATSFiles")
    public void step69_listAzurePipelines() {
        System.out.println("▶ Step 69: List Azure Pipelines");
        new tests.pipeline.ListAzurePipelinesTest().listAzurePipelines();
    }

    @Test(dependsOnMethods = "step69_listAzurePipelines")
    public void step70_executeAutomationTests() {
        System.out.println("▶ Step 70: Execute Automation Tests");
        new ExecuteTestsTest().executeAutomationTests();
    }

    @Test(dependsOnMethods = "step70_executeAutomationTests")
    public void step71_validateExecutionState() {
        System.out.println("▶ Step 71: Validate Execution State");
        new tests.pipeline.ValidateExecutionPollingTest().validateExecutionState();
    }

    @Test(dependsOnMethods = "step71_validateExecutionState")
    public void step72_downloadATSVideo() {
        System.out.println("▶ Step 72: Download Automation Execution Video");
        new GetAutomationVideoTest().getAutomationVideo();
    }

    @Test(dependsOnMethods = "step72_downloadATSVideo")
    public void step73_checkVersion() {
        System.out.println("▶ Step 73: Check System Version");
        new GetVersionTest().getVersionApiTest();
    }

    @Test(dependsOnMethods = "step73_checkVersion")
    public void step74_getLogFiles() {
        System.out.println("▶ Step 74: Get Log Files");
        new GetLogFilesTest().getLogFilesApiTest();
    }

    @Test(dependsOnMethods = "step74_getLogFiles")
    public void step75_downloadLogFiles() {
        System.out.println("▶ Step 75: Download Log Files");
        new DownloadLogFileTest().downloadLogFileApiTest();
    }

    @Test(dependsOnMethods = "step75_downloadLogFiles")
    public void step76_logOut() {
        System.out.println("▶ Step 75: Log Out User");
        new LogOutTest().logoutTest();

        // Cleanup Stores
        ProjectStore.clear();
        GeneratedTSStore.clear();
        TestScenarioStore.clear();
        TestCaseStore.clear();
        ATSStore.clear();
        BusinessRequirementStore.clear();
        LogStore.clear();
    }
}