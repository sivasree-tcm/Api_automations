package tests.flow;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.ats.GetAutomationVideoTest;
import tests.ats.LoadATSFilesTest;
import tests.connection.DbConfigTest;
import tests.export.ExportTCExcelTest;
import tests.framework.DownloadAtsFrameworkTest;
import tests.generation.*;
import tests.generation.GetGenerationStatusTest;
import tests.project.*;
import tests.modelmapping.GetLlmModelsTest;
import tests.modelmapping.MapLlmToProjectTest;
import tests.br.UploadBusinessRequirementTest;
import tests.project.GenerateTSTest;
import tests.sprints.GetAzureSprintsTest;
import tests.sprints.GetUserStoriesTest;
import tests.sprints.ImportAzureUserStoriesTest;
import tests.uploadFiles.DeleteFilesTest;
import tests.uploadFiles.DownloadFilesTest;
import tests.uploadFiles.UploadFilesForBRTest;
import tests.userManagement.GetUserManagementDetailsTest;
import utils.*;

public class EndToEndFlow extends BaseTest {

    @Test
    public void step1_createProject() {
        System.out.println("▶ Step 1: Create Project");
        new createprojectflow().projectApiTest();
    }


    @Test(dependsOnMethods = "step1_createProject")
    public void step2_AddDbConfig() {
        System.out.println("▶ Step 2: Add DB Config");
        new DbConfigTest().addDbInfoTest();
    }

    @Test(dependsOnMethods = "step2_AddDbConfig")
    public void step3_getProjectDetails() {
        System.out.println("▶ Step 3: Get Project Details");
        new GetProjectDetailsTest().fetchProjectDetails(ProjectStore.getProjectId());
    }

    @Test(dependsOnMethods = "step3_getProjectDetails")
    public void step4_getModels() {
        System.out.println("▶ Step 4: Get LLM Models");
        new GetLlmModelsTest().fetchAndStoreModels();
    }

    @Test(dependsOnMethods = "step4_getModels")
    public void step5_mapModels() {
        System.out.println("▶ Step 5: Map Models");
        new MapLlmToProjectTest().mapLlmToProjectTest();
    }

    @Test(dependsOnMethods = "step5_mapModels")
    public void step6_getLlmCredentials() {
        System.out.println("▶ Step 6: Get LLM Credentials");
        new GetLlmCredentialsTest().fetchLlmCredentials();
    }

    @Test(dependsOnMethods = "step6_getLlmCredentials")
    public void step7_mapCredentialToProject() {
        System.out.println("▶ Step 7: Map Credential To Project");
        new MapCredentialToProjectTest().mapCredentialToProjectApiTest();
    }

    @Test(dependsOnMethods = "step7_mapCredentialToProject")
    public void step8_getProviderConfig() {
        System.out.println("▶ Step 8: Get Provider Config");
        new GetProjectProviderConfigTest().getProjectProviderConfigApiTest();
    }

    @Test(dependsOnMethods = "step8_getProviderConfig")
    public void step9_getEnvironmentDetails() {
        System.out.println("▶ Step 9: Get Environment Details");
        new tests.connection.EnvironmentDetailsTest().fetchEnvironmentDetailsTest();
    }

    @Test(dependsOnMethods = "step9_getEnvironmentDetails")
    public void step10_saveProjectConfig() {
        System.out.println("▶ Step 10: Save Project Config");
        new tests.connection.SaveProjectConfigTest().saveConfigTest();
    }

    @Test(dependsOnMethods = "step10_saveProjectConfig")
    public void step11_createRole() {
        System.out.println("▶ Step 11: Create Role");
        new CreateRole().createRoleTest();
    }

    @Test(dependsOnMethods = "step11_createRole")
    public void step12_addUser() {
        System.out.println("▶ Step 12: Add User");
        new AddUpdateProjecTest().addUpdateProjectUserApiTest();
    }

    @Test(dependsOnMethods = "step12_addUser")
    public void step13_getUserManagementDetails() {
        System.out.println("▶ Step 13: Get User Management Details");
        new GetUserManagementDetailsTest().getUserManagementDetails();
    }

    @Test(dependsOnMethods = "step13_getUserManagementDetails")
    public void step14_createPrompt() {
        System.out.println("▶ Step 14: Create Prompt");
        new createprompt().createPromptApiTest();
    }

    @Test(dependsOnMethods = "step14_createPrompt")
    public void step15_mapPrompt() {
        System.out.println("▶ Step 15: Map Prompt");
        new mapprompt().mapPromptApiTest();
    }

    @Test(dependsOnMethods = "step15_mapPrompt")
    public void step16_getAzureSprints() {
        System.out.println("▶ Step 16: Get Azure Sprints");
        new GetAzureSprintsTest().getAzureDevOpsSprints();
    }

    @Test(dependsOnMethods = "step16_getAzureSprints")
    public void step17_getUserStories() {
        System.out.println("▶ Step 17: Get User Stories");
        new GetUserStoriesTest().getUserStories();
    }

    @Test(dependsOnMethods = "step17_getUserStories")
    public void step18_importAzureUserStories() {
        System.out.println("▶ Step 18: Import Azure User Stories");
        new ImportAzureUserStoriesTest().importAzureUserStories();
    }

    @Test(dependsOnMethods = "step18_importAzureUserStories")
    public void step19_uploadBusinessRequirement() {
        System.out.println("▶ Step 19: Upload Business Requirement");
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step19_uploadBusinessRequirement")
    public void step20_exportBRExcel() throws Exception {
        System.out.println("▶ Step 20: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }
    @Test(dependsOnMethods = "step20_exportBRExcel")
    public void step21_deletebr() throws Exception {
        System.out.println("▶ Step 21: Export BR Excel");
        new tests.br.DeleteBusinessRequirementoneTest().deleteLastBR();
    }

    @Test(dependsOnMethods = "step21_deletebr")
    public void step22_getBRsRefresh() {
        System.out.println("▶ Step 21: Refresh BRs");
        BusinessRequirementStore.clear();
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    // ✅ INSERTED HERE
    @Test(dependsOnMethods = "step22_getBRsRefresh")
    public void step23_importImageForBR() {
        System.out.println("▶ Step 22: Import Image For BR");
        new UploadFilesForBRTest().uploadImageForALLBRs();
    }

    @Test(dependsOnMethods = "step23_importImageForBR")
    public void step24_downloadimage() {
        System.out.println("▶ Step 22: Import Image For BR");
        new DownloadFilesTest().downloadSingleImageForBR();
    }
    @Test(dependsOnMethods = "step24_downloadimage")
    public void step25_deleteimage() {
        System.out.println("▶ Step 22: Import Image For BR");
        new DeleteFilesTest().deleteSingleImageForBR();
    }


    @Test(dependsOnMethods = "step25_deleteimage")
    public void step26_generateTSFinal() {
        System.out.println("▶ Step 23: Generate TS");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step26_generateTSFinal")
    public void step27_waitForTSGeneration() {
        System.out.println("▶ Step 24: Wait For TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step27_waitForTSGeneration")
    public void step28_getTSByBR() {
        System.out.println("▶ Step 25: Get TS By BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }


    @Test(dependsOnMethods = "step28_getTSByBR")
    public void step29_exportTSExcel() throws Exception {
        System.out.println("▶ Step 26: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step29_exportTSExcel")
    public void step30_deleteTestScenario() {
        System.out.println("▶ Step 27: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step30_deleteTestScenario")
    public void step31_addTestScenario() {
        System.out.println("▶ Step 28: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step31_addTestScenario")
    public void step32_updateTestScenario() {
        System.out.println("▶ Step 29: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step32_updateTestScenario")
    public void step33_generateTC() {
        System.out.println("▶ Step 30: Generate TC");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step33_generateTC")
    public void step34_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 31: Wait For TC Completion");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step34_waitForTCCompletion")
    public void step35_exportTC() {
        System.out.println("▶ Step 32: Export TC");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step35_exportTC")
    public void step36_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 33: Get TC Summary");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step36_getTestCaseSummaryForTS")
    public void step37_getTestCaseWithSteps() {
        System.out.println("▶ Step 34: Get TC With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step37_getTestCaseWithSteps")
    public void step38_addTestCaseStep() {
        System.out.println("▶ Step 35: Add TC Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step38_addTestCaseStep")
    public void step39_updateTestCaseStep() {
        System.out.println("▶ Step 36: Update TC Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step39_updateTestCaseStep")
    public void step40_deleteTestCaseStep() {
        System.out.println("▶ Step 37: Delete TC Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step40_deleteTestCaseStep")
    public void step41_updateTestCase() {
        System.out.println("▶ Step 38: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step41_updateTestCase")
    public void step42_addTestCase() {
        System.out.println("▶ Step 39: Add Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }
    @Test(dependsOnMethods = "step42_addTestCase")
    public void step43_addbrr() {
        System.out.println("▶ Step 39: Add Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }


    @Test(dependsOnMethods = "step43_addbrr")
    public void step44_deleteTestCase() {
        System.out.println("▶ Step 40: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step44_deleteTestCase")
    public void step45_generateAutomationCode() {
        System.out.println("▶ Step 41: Generate Automation Code");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step45_generateAutomationCode")
    public void step46_checkATSStatus() {
        System.out.println("▶ Step 42: Check ATS Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step46_checkATSStatus")
    public void step47_downloadATSFramework() {
        System.out.println("▶ Step 43: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
    }

    @Test(dependsOnMethods = "step47_downloadATSFramework")
    public void step48_loadATSFiles() {
        System.out.println("▶ Step 44: Load ATS Files");
        new LoadATSFilesTest().loadATSFiles();
    }

    @Test(dependsOnMethods = "step48_loadATSFiles")
    public void step49_listAzurePipelines() {
        System.out.println("▶ Step 45: List Azure Pipelines");
        new tests.pipeline.ListAzurePipelinesTest().listAzurePipelines();
    }

    @Test(dependsOnMethods = "step49_listAzurePipelines")
    public void step50_executeAutomationTests() {
        System.out.println("▶ Step 46: Execute Automation Tests");
        new tests.connection.ExecuteTestsTest().executeAutomationTests();
    }

    @Test(dependsOnMethods = "step50_executeAutomationTests")
    public void step51_validateExecutionState() {
        System.out.println("▶ Step 47: Validate Execution State");
        new tests.pipeline.ValidateExecutionPollingTest().validateExecutionState();
    }

    @Test(dependsOnMethods = "step51_validateExecutionState")
    public void step52_downloadATSVideo() {
        System.out.println("▶ Step 48: Download ATS Video");
        new GetAutomationVideoTest().getAutomationVideo();
        ProjectStore.clear();
        GeneratedTSStore.clear();
        TestScenarioStore.clear();
        TestCaseStore.clear();
        ATSStore.clear();
        BusinessRequirementStore.clear();
    }
}