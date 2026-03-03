package tests.flow;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.ats.LoadATSFilesTest;
import tests.bdd.UpdateBddTest;
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
import utils.*;

public class newprojectflow extends BaseTest {

    @Test
    public void step1_createProject() {
        new createprojectflow().projectApiTest();
    }

    @Test(dependsOnMethods = "step1_createProject")
    public void step2_AddDbConfig() {
        new DbConfigTest().addDbInfoTest();
    }

    @Test(dependsOnMethods = "step2_AddDbConfig")
    public void step3_getProjectDetails() {
        Integer dynamicProjectId = ProjectStore.getProjectId();
        if (dynamicProjectId == null) {
            throw new RuntimeException("❌ Failure: Project ID not found in ProjectStore.");
        }
        System.out.println("▶ Step 3: Fetching Details for Dynamic Project ID: " + dynamicProjectId);
        new GetProjectDetailsTest().fetchProjectDetails(dynamicProjectId);
    }

    @Test(dependsOnMethods = "step3_getProjectDetails")
    public void step4_getModels() {
        new GetLlmModelsTest().fetchAndStoreModels();
    }

    @Test(dependsOnMethods = "step4_getModels")
    public void step5_mapModels() {
        new MapLlmToProjectTest().mapLlmToProjectTest();
    }

    @Test(dependsOnMethods = "step5_mapModels")
    public void step6_getLlmCredentials() {
        new GetLlmCredentialsTest().fetchLlmCredentials();
    }

    @Test(dependsOnMethods = "step6_getLlmCredentials")
    public void step7_mapCredentialToProject() {
        new MapCredentialToProjectTest().mapCredentialToProjectApiTest();
    }

    @Test(dependsOnMethods = "step7_mapCredentialToProject")
    public void step8_getProviderConfig() {
        new GetProjectProviderConfigTest().getProjectProviderConfigApiTest();
    }

    @Test(dependsOnMethods = "step8_getProviderConfig")
    public void step9_getEnvironmentDetails() {
        System.out.println("▶ Step 9: Get Environment Details");
        new tests.connection.EnvironmentDetailsTest().fetchEnvironmentDetailsTest();
    }

    @Test(dependsOnMethods = "step9_getEnvironmentDetails")
    public void step10_saveProjectConfig() {
        System.out.println("▶ Step 10: Save Project Configuration");
        new tests.connection.SaveProjectConfigTest().saveConfigTest();
    }

    @Test(dependsOnMethods = "step10_saveProjectConfig")
    public void step11_createRole() {
        new CreateRole().createRoleTest();
    }

    @Test(dependsOnMethods = "step11_createRole")
    public void step12_addUser() {
        new AddUpdateProjecTest().addUpdateProjectUserApiTest();
    }

    @Test(dependsOnMethods = "step12_addUser")
    public void step13_createPrompt() {
        new createprompt().createPromptApiTest();
    }

    @Test(dependsOnMethods = "step13_createPrompt")
    public void step14_mapPrompt() {
        new mapprompt().mapPromptApiTest();
    }

    @Test(dependsOnMethods = "step14_mapPrompt")
    public void step15_getBRs() {
        System.out.println("▶ Step 15: Get BRs");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step15_getBRs")
    public void step16_getAzureSprints() {
        System.out.println("▶ Step 16: Get Azure DevOps Sprints");
        SprintStore.clear();
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
    public void step19_getBRsAfterImport() {
        System.out.println("▶ Step 19: Get BRs After Import");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step19_getBRsAfterImport")
    public void step20_uploadBusinessRequirement() {
        System.out.println("▶ Step 20: Upload Business Requirement");
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step20_uploadBusinessRequirement")
    public void step21_exportBRExcel() throws Exception {
        System.out.println("▶ Step 21: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step21_exportBRExcel")
    public void step22_generateTS() {
        System.out.println("▶ Step 22: Generate Test Scenarios");
        new tests.generation.GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step22_generateTS")
    public void step23_getBRsRefresh() {
        System.out.println("▶ Step 23: Get Business Requirements Refresh");
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step23_getBRsRefresh")
    public void step24_generateTSFinal() {
        System.out.println("▶ Step 24: Generate Test Scenarios (TS) Final");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step24_generateTSFinal")
    public void step25_waitForTSGeneration() {
        System.out.println("▶ Step 25: Wait for TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step25_waitForTSGeneration")
    public void step26_getTSByBR() {
        System.out.println("▶ Step 26: Get TS by BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step26_getTSByBR")
    public void step27_exportTSExcel() throws Exception {
        System.out.println("▶ Step 27: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step27_exportTSExcel")
    public void step28_deleteTestScenario() {
        System.out.println("▶ Step 28: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step28_deleteTestScenario")
    public void step29_addTestScenario() {
        System.out.println("▶ Step 29: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step29_addTestScenario")
    public void step30_updateTestScenario() {
        System.out.println("▶ Step 30: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step30_updateTestScenario")
    public void step31_generateTC() {
        System.out.println("▶ Step 31: Generate Test Cases (TC)");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step31_generateTC")
    public void step32_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 32: Wait for TC Generation");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step32_waitForTCCompletion")
    public void step33_exportTC() {
        System.out.println("▶ Step 33: Export TC Excel");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step33_exportTC")
    public void step34_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 34: Get Test Case Summary for TS");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step34_getTestCaseSummaryForTS")
    public void step35_getTestCaseWithSteps() {
        System.out.println("▶ Step 35: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step35_getTestCaseWithSteps")
    public void step36_addTestCaseStep() {
        System.out.println("▶ Step 36: Add Test Case Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step36_addTestCaseStep")
    public void step37_updateTestCaseStep() {
        System.out.println("▶ Step 37: Update Test Case Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step37_updateTestCaseStep")
    public void step38_deleteTestCaseStep() {
        System.out.println("▶ Step 38: Delete Test Case Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }

//    @Test(dependsOnMethods = "step38_deleteTestCaseStep")
//    public void step39_updateTestCaseStepOrder() {
//        System.out.println("▶ Step 39: Update Test Case Step Order");
//        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
//    }

    @Test(dependsOnMethods = "step38_deleteTestCaseStep")
    public void step40_updateTestCase() {
        System.out.println("▶ Step 40: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }
    @Test(dependsOnMethods = "step38_deleteTestCaseStep")
    public void step41_updatebdd() {
        System.out.println("▶ Step 40: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step41_updatebdd")
    public void step42_addTestCase() {
        System.out.println("▶ Step 41: Add Test Case");
        new UpdateBddTest().updateBdd();
    }

    @Test(dependsOnMethods = "step42_addTestCase")
    public void step43_deleteTestCase() {
        System.out.println("▶ Step 42: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step43_deleteTestCase")
    public void step44_generateAutomationCode() {
        System.out.println("▶ Step 43: Generate Automation Code");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step44_generateAutomationCode")
    public void step45_checkATSStatus() {
        System.out.println("▶ Step 44: Check the ATS Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step45_checkATSStatus")
    public void step46_downloadATSFramework() {
        System.out.println("▶ Step 45: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
    }

    @Test(dependsOnMethods = "step46_downloadATSFramework")
    public void step47_loadATSFiles() {
        System.out.println("▶ Step 46: Load ATS Files");
        new LoadATSFilesTest().loadATSFiles();
    }


    @Test(dependsOnMethods = "step47_loadATSFiles")
    public void step48_listAzurePipelines() {
        System.out.println("▶ Step 28: List Azure Pipelines");

        // Instantiate the class and call the logic
        tests.pipeline.ListAzurePipelinesTest pipelineTest = new tests.pipeline.ListAzurePipelinesTest();
        pipelineTest.listAzurePipelines();
    }
    @Test(dependsOnMethods = "step48_listAzurePipelines")
    public void step49_executeAutomationTests() {
        System.out.println("▶ Step 48: Triggering Automation Execution");

        // Instantiate the class and call the logic
        tests.connection.ExecuteTestsTest executeTest = new tests.connection.ExecuteTestsTest();
        executeTest.executeAutomationTests();
    }
    @Test(dependsOnMethods = "step49_executeAutomationTests")
    public void step50_validateExecutionState() {
        System.out.println("▶ Step 49: Polling Execution Result State");

        // Instantiate the class and call the logic
        tests.pipeline.ValidateExecutionPollingTest pollingTest = new tests.pipeline.ValidateExecutionPollingTest();

        // Call the logic to monitor the execution until 'passed' or 'failed'
        pollingTest.validateExecutionState();
    }


}