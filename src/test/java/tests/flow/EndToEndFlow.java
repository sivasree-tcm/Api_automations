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
import tests.modelmapping.GetLlmCredentialsTest;
import tests.modelmapping.MapCredentialToProjectTest;
import tests.pipeline.ExecuteTestsTest;
import tests.roles.CreateRoleTest;
import tests.testCase.*;
import tests.testScenario.AddTestScenarioTest;
import tests.testScenario.DeleteTestScenarioTest;
import tests.testScenario.GetTSByBRTest;
import tests.testScenario.UpdateTestScenarioTest;
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
import tests.roles.CheckUserRolesTest;
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
    public void step23_getUserManagementDetails() {
        System.out.println("▶ Step 23: Get User Management Details");
        new GetUserManagementDetailsTest().getUserManagementDetails();
    }

    @Test(dependsOnMethods = "step23_getUserManagementDetails")
    public void step24_createPrompt() {
        System.out.println("▶ Step 24: Create Prompt");
        new CreatePromptTest().createPromptApiTest();
    }

    @Test(dependsOnMethods = "step24_createPrompt")
    public void step25_updatePrompt() {
        System.out.println("▶ Step 25: Update Prompt");
        new UpdatePromptTest().updatePromptApiTest();
    }

    @Test(dependsOnMethods = "step25_updatePrompt")
    public void step26_mapPrompt() {
        System.out.println("▶ Step 26: Map Prompt");
        new MapPromptTest().mapPromptApiTest();
    }

    @Test(dependsOnMethods = "step26_mapPrompt")
    public void step27_getAzureSprints() {
        System.out.println("▶ Step 27: Get Azure Sprints");
        new GetAzureSprintsTest().getAzureDevOpsSprints();
    }

    @Test(dependsOnMethods = "step27_getAzureSprints")
    public void step28_getUserStories() {
        System.out.println("▶ Step 28: Get User Stories");
        new GetUserStoriesTest().getUserStories();
    }

    @Test(dependsOnMethods = "step28_getUserStories")
    public void step29_importAzureUserStories() {
        System.out.println("▶ Step 29: Import Azure User Stories");
        new ImportAzureUserStoriesTest().importAzureUserStories();
    }

    @Test(dependsOnMethods = "step29_importAzureUserStories")
    public void step30_uploadBusinessRequirement() {
        System.out.println("▶ Step 30: Upload Business Requirement");
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step30_uploadBusinessRequirement")
    public void step31_exportBRExcel() throws Exception {
        System.out.println("▶ Step 31: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step31_exportBRExcel")
    public void step32_deleteBR() throws Exception {
        System.out.println("▶ Step 32: Delete Business Requirement");
        new DeleteBusinessRequirementTest().deleteLastBR();
    }

    @Test(dependsOnMethods = "step32_deleteBR")
    public void step33_refreshBRs() {
        System.out.println("▶ Step 33: Refresh Business Requirements");
        BusinessRequirementStore.clear();
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step33_refreshBRs")
    public void step34_checkGCSForBR() {
        System.out.println("▶ Step 34: Check GCS Path For BR");
        new CheckGcsPathForBrTest().checkGcsPathForBrApiTest();
    }

    @Test(dependsOnMethods = "step34_checkGCSForBR")
    public void step35_importImageForBR() {
        System.out.println("▶ Step 35: Upload Image For BR");
        new UploadFilesForBRTest().uploadImageForALLBRs();
    }

    @Test(dependsOnMethods = "step35_importImageForBR")
    public void step36_listFiles() {
        System.out.println("▶ Step 36: List Files");
        new ListFilesTest().listFiles();
    }

    @Test(dependsOnMethods = "step36_listFiles")
    public void step37_downloadImage() {
        System.out.println("▶ Step 37: Download Image");
        new DownloadFilesTest().downloadSingleImageForBR();
    }

    @Test(dependsOnMethods = "step37_downloadImage")
    public void step38_deleteImage() {
        System.out.println("▶ Step 38: Delete Image");
        new DeleteFilesTest().deleteSingleImageForBR();
    }

    @Test(dependsOnMethods = "step38_deleteImage")
    public void step39_generateTS() {
        System.out.println("▶ Step 39: Generate Test Scenarios");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step39_generateTS")
    public void step40_waitForTSGeneration() {
        System.out.println("▶ Step 40: Wait For TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step40_waitForTSGeneration")
    public void step41_getTSByBR() {
        System.out.println("▶ Step 41: Get Test Scenarios By BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step41_getTSByBR")
    public void step42_exportTSExcel() throws Exception {
        System.out.println("▶ Step 42: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step42_exportTSExcel")
    public void step43_deleteTestScenario() {
        System.out.println("▶ Step 43: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step43_deleteTestScenario")
    public void step44_addTestScenario() {
        System.out.println("▶ Step 44: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step44_addTestScenario")
    public void step45_updateTestScenario() {
        System.out.println("▶ Step 45: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step45_updateTestScenario")
    public void step46_generateTC() {
        System.out.println("▶ Step 46: Generate Test Cases");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step46_generateTC")
    public void step47_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 47: Wait For TC Completion");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step47_waitForTCCompletion")
    public void step48_exportTC() {
        System.out.println("▶ Step 48: Export Test Cases");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step48_exportTC")
    public void step49_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 49: Get Test Case Summary For TS");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step49_getTestCaseSummaryForTS")
    public void step50_getTestCaseWithSteps() {
        System.out.println("▶ Step 50: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step50_getTestCaseWithSteps")
    public void step51_addTestCaseStep() {
        System.out.println("▶ Step 51: Add Test Case Step");
        new AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step51_addTestCaseStep")
    public void step52_updateTestCaseStep() {
        System.out.println("▶ Step 52: Update Test Case Step");
        new UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step52_updateTestCaseStep")
    public void step53_deleteTestCaseStep() {
        System.out.println("▶ Step 53: Delete Test Case Step");
        new DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step53_deleteTestCaseStep")
    public void step54_updateTestCase() {
        System.out.println("▶ Step 54: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step54_updateTestCase")
    public void step55_addTestCase() {
        System.out.println("▶ Step 55: Add Test Case");
        new InsertTestCaseTest().addTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step55_addTestCase")
    public void step56_updateBDD() {
        System.out.println("▶ Step 56: Update BDD");
        new UpdateBddTest().updateBdd();
    }

    @Test(dependsOnMethods = "step56_updateBDD")
    public void step57_updateTestCaseStepOrder() {
        System.out.println("▶ Step 57: Update Test Case Step Order");
        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
    }

    @Test(dependsOnMethods = "step57_updateTestCaseStepOrder")
    public void step58_deleteTestCase() {
        System.out.println("▶ Step 58: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step58_deleteTestCase")
    public void step59_generateAutomationCode() {
        System.out.println("▶ Step 59: Generate Automation Code (ATS)");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step59_generateAutomationCode")
    public void step60_checkATSStatus() {
        System.out.println("▶ Step 60: Check ATS Generation Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step60_checkATSStatus")
    public void step61_downloadATSFramework() {
        System.out.println("▶ Step 61: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
    }

    @Test(dependsOnMethods = "step61_downloadATSFramework")
    public void step62_loadATSFiles() {
        System.out.println("▶ Step 62: Load ATS Files");
        new LoadATSFilesTest().loadATSFiles();
    }

    @Test(dependsOnMethods = "step62_loadATSFiles")
    public void step63_listAzurePipelines() {
        System.out.println("▶ Step 63: List Azure Pipelines");
        new tests.pipeline.ListAzurePipelinesTest().listAzurePipelines();
    }

    @Test(dependsOnMethods = "step63_listAzurePipelines")
    public void step64_executeAutomationTests() {
        System.out.println("▶ Step 64: Execute Automation Tests");
        new ExecuteTestsTest().executeAutomationTests();
    }

    @Test(dependsOnMethods = "step64_executeAutomationTests")
    public void step65_validateExecutionState() {
        System.out.println("▶ Step 65: Validate Execution State");
        new tests.pipeline.ValidateExecutionPollingTest().validateExecutionState();
    }

    @Test(dependsOnMethods = "step65_validateExecutionState")
    public void step66_downloadATSVideo() {
        System.out.println("▶ Step 66: Download Automation Execution Video");
        new GetAutomationVideoTest().getAutomationVideo();
    }

    @Test(dependsOnMethods = "step66_downloadATSVideo")
    public void step67_checkVersion() {
        System.out.println("▶ Step 67: Check System Version");
        new GetVersionTest().getVersionApiTest();
    }

    @Test(dependsOnMethods = "step67_checkVersion")
    public void step68_getLogFiles() {
        System.out.println("▶ Step 68: Get Log Files");
        new GetLogFilesTest().getLogFilesApiTest();
    }

    @Test(dependsOnMethods = "step68_getLogFiles")
    public void step69_downloadLogFiles() {
        System.out.println("▶ Step 69: Download Log Files");
        new DownloadLogFileTest().downloadLogFileApiTest();

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