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
    public void step21_getBRsRefresh() {
        System.out.println("▶ Step 21: Refresh BRs");
        BusinessRequirementStore.clear();
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    // ✅ INSERTED HERE
    @Test(dependsOnMethods = "step21_getBRsRefresh")
    public void step22_importImageForBR() {
        System.out.println("▶ Step 22: Import Image For BR");
        new UploadFilesForBRTest().uploadImageForALLBRs();
    }

    @Test(dependsOnMethods = "step22_importImageForBR")
    public void step23_generateTSFinal() {
        System.out.println("▶ Step 23: Generate TS");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step23_generateTSFinal")
    public void step24_waitForTSGeneration() {
        System.out.println("▶ Step 24: Wait For TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step24_waitForTSGeneration")
    public void step25_getTSByBR() {
        System.out.println("▶ Step 25: Get TS By BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }


    @Test(dependsOnMethods = "step25_getTSByBR")
    public void step26_exportTSExcel() throws Exception {
        System.out.println("▶ Step 26: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step26_exportTSExcel")
    public void step27_deleteTestScenario() {
        System.out.println("▶ Step 27: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step27_deleteTestScenario")
    public void step28_addTestScenario() {
        System.out.println("▶ Step 28: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step28_addTestScenario")
    public void step29_updateTestScenario() {
        System.out.println("▶ Step 29: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step29_updateTestScenario")
    public void step30_generateTC() {
        System.out.println("▶ Step 30: Generate TC");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step30_generateTC")
    public void step31_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 31: Wait For TC Completion");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step31_waitForTCCompletion")
    public void step32_exportTC() {
        System.out.println("▶ Step 32: Export TC");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step32_exportTC")
    public void step33_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 33: Get TC Summary");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step33_getTestCaseSummaryForTS")
    public void step34_getTestCaseWithSteps() {
        System.out.println("▶ Step 34: Get TC With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step34_getTestCaseWithSteps")
    public void step35_addTestCaseStep() {
        System.out.println("▶ Step 35: Add TC Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step35_addTestCaseStep")
    public void step36_updateTestCaseStep() {
        System.out.println("▶ Step 36: Update TC Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step36_updateTestCaseStep")
    public void step37_deleteTestCaseStep() {
        System.out.println("▶ Step 37: Delete TC Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step37_deleteTestCaseStep")
    public void step38_updateTestCase() {
        System.out.println("▶ Step 38: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step38_updateTestCase")
    public void step39_addTestCase() {
        System.out.println("▶ Step 39: Add Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step39_addTestCase")
    public void step40_deleteTestCase() {
        System.out.println("▶ Step 40: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step40_deleteTestCase")
    public void step41_generateAutomationCode() {
        System.out.println("▶ Step 41: Generate Automation Code");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step41_generateAutomationCode")
    public void step42_checkATSStatus() {
        System.out.println("▶ Step 42: Check ATS Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step42_checkATSStatus")
    public void step43_downloadATSFramework() {
        System.out.println("▶ Step 43: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
    }

    @Test(dependsOnMethods = "step43_downloadATSFramework")
    public void step44_loadATSFiles() {
        System.out.println("▶ Step 44: Load ATS Files");
        new LoadATSFilesTest().loadATSFiles();
    }

    @Test(dependsOnMethods = "step44_loadATSFiles")
    public void step45_listAzurePipelines() {
        System.out.println("▶ Step 45: List Azure Pipelines");
        new tests.pipeline.ListAzurePipelinesTest().listAzurePipelines();
    }

    @Test(dependsOnMethods = "step45_listAzurePipelines")
    public void step46_executeAutomationTests() {
        System.out.println("▶ Step 46: Execute Automation Tests");
        new tests.connection.ExecuteTestsTest().executeAutomationTests();
    }

    @Test(dependsOnMethods = "step46_executeAutomationTests")
    public void step47_validateExecutionState() {
        System.out.println("▶ Step 47: Validate Execution State");
        new tests.pipeline.ValidateExecutionPollingTest().validateExecutionState();
    }

    @Test(dependsOnMethods = "step47_validateExecutionState")
    public void step48_downloadATSVideo() {
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