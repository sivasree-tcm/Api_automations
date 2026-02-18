//package tests.project;
//
//import api.generation.GenerateATSApi;
//import base.BaseTest;
//import org.testng.annotations.Test;
//import tests.connection.DbConfigTest;
//import tests.connection.saveconnectionflow;
//import tests.export.ExportTCExcelTest;
//import tests.generation.*;
//import tests.generation.GetGenerationStatusTest;
//import tests.project.createprojectflow;
//import tests.modelmapping.GetLlmModelsTest;
//import tests.modelmapping.MapLlmToProjectTest;
//import tests.project.GetLlmCredentialsTest;
//import tests.project.MapCredentialToProjectTest;
//import tests.project.GetProjectProviderConfigTest;
//import tests.project.CreateRole;
//import tests.project.AddUpdateProjecTest;
//import tests.project.createprompt;
//import tests.project.mapprompt;
//import tests.br.UploadBusinessRequirementTest;
//import tests.project.GetBusinessRequirementTest;
//import tests.project.GenerateTSTest;
//import tests.sprints.GetAzureSprintsTest;
//import tests.sprints.GetUserStoriesTest;
//import tests.sprints.ImportAzureUserStoriesTest;
//import utils.BusinessRequirementStore;
//import utils.SprintStore;
//
///**
// * Full End-to-End Flow: From Connection Setup to Code Generation.
// */
//public class ConnectionToProjectFlow extends BaseTest {
//
////    @Test
////    public void step1_saveConnection() {
////        new saveconnectionflow().saveConnectionTest();
////    }
//
////    @Test(dependsOnMethods = "step1_saveConnection")
//
//    public void step2_createProject() {
//        new createprojectflow().projectApiTest();
//    }
//
//    @Test(dependsOnMethods = "step2_createProject")
//    public void step3_AddDbConfig() {
//        new DbConfigTest().addDbInfoTest();
//    }
//
//    @Test(dependsOnMethods = "step3_AddDbConfig")
//    public void step4_getModels() {
//        new GetLlmModelsTest().fetchAndStoreModels();
//    }
//
//    @Test(dependsOnMethods = "step4_getModels")
//    public void step5_mapModels() {
//        new MapLlmToProjectTest().mapLlmToProjectTest();
//    }
//
//    @Test(dependsOnMethods = "step5_mapModels")
//    public void step6_getLlmCredentials() {
//        new GetLlmCredentialsTest().fetchLlmCredentials();
//    }
//
//    @Test(dependsOnMethods = "step6_getLlmCredentials")
//    public void step7_mapCredentialToProject() {
//        new MapCredentialToProjectTest().mapCredentialToProjectApiTest();
//    }
//
//    @Test(dependsOnMethods = "step7_mapCredentialToProject")
//    public void step8_getProviderConfig() {
//        new GetProjectProviderConfigTest().getProjectProviderConfigApiTest();
//    }
//
//    @Test(dependsOnMethods = "step8_getProviderConfig")
//    public void step9_createRole() {
//        new CreateRole().createRoleTest();
//    }
//
//    @Test(dependsOnMethods = "step9_createRole")
//    public void step10_addUser() {
//        new AddUpdateProjecTest().addUpdateProjectUserApiTest();
//    }
//
//    @Test(dependsOnMethods = "step10_addUser")
//    public void step11_createPrompt() {
//        new createprompt().createPromptApiTest();
//    }
//
//    @Test(dependsOnMethods = "step11_createPrompt")
//    public void step12_mapPrompt() {
//        new mapprompt().mapPromptApiTest();
//    }
//
//
//    @Test(dependsOnMethods = "step12_mapPrompt")
//    public void step13_getAzureSprints() {
//        System.out.println("▶ Step 6: Get Azure DevOps Sprints");
//        SprintStore.clear();
//        new GetAzureSprintsTest().getAzureDevOpsSprints();
//    }
//
//    @Test(dependsOnMethods = "step13_getAzureSprints")
//    public void step14_getUserStories() {
//        System.out.println("▶ Step 7: Get User Stories");
//        new GetUserStoriesTest().getUserStories();
//    }
//
//    @Test(dependsOnMethods = "step14_getUserStories")
//    public void step15_importAzureUserStories() {
//        System.out.println("▶ Step 8: Import Azure User Stories");
//        new ImportAzureUserStoriesTest().importAzureUserStories();
//    }
//
//    @Test(dependsOnMethods = "step15_importAzureUserStories")
//    public void step16_getBRs() {
//        System.out.println("▶ Step 9: Get BRs");
//        BusinessRequirementStore.clear();
//        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
//    }
//    @Test(dependsOnMethods = "step16_getBRs")
//    public void step17_uploadBusinessRequirement() {
//        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
//    }
//    @Test(dependsOnMethods = "step17_uploadBusinessRequirement")
//    public void step18_uploadFilesForBR() {
//        System.out.println("▶ Step 10: Image Upload Files for BR");
//        new tests.uploadFiles.UploadFilesForBRTest().uploadImageForALLBRs();
//    }
//
//    @Test(dependsOnMethods = "step18_uploadFilesForBR")
//    public void step19_exportBRExcel() throws Exception {
//        System.out.println("▶ Step 11: Export BR Excel");
//        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
//    }
//
//    @Test(dependsOnMethods = "step19_exportBRExcel")
//    public void step20_generateTS() {
//        System.out.println("▶ Step 12: Generate Test Scenarios");
//        new tests.generation.GenerateTSTest().generateTSForBR();
//    }
//
//
//    @Test(dependsOnMethods = "step20_generateTS")
//    public void step21_getBRs() {
//        System.out.println("▶ Step 14: Get Business Requirements");
//        new GetBusinessRequirementTest().fetchBRsForProject();
//    }
//
//    @Test(dependsOnMethods = "step21_getBRs")
//    public void step22_generateTS() {
//        System.out.println("▶ Step 15: Generate Test Scenarios (TS)");
//        new GenerateTSTest().generateTSForBR();
//    }
//
//    @Test(dependsOnMethods = "step22_generateTS")
//    public void step23_waitForTSGeneration() {
//        System.out.println("▶ Step 16: Wait for TS Generation");
//        new GetGenerationStatusTest().waitUntilAllCompleted();
//    }
//
//    @Test(dependsOnMethods = "step23_waitForTSGeneration")
//    public void step24_getTSByBR() {
//        System.out.println("▶ Step 17: Get TS by BR");
//        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
//    }
//    @Test(dependsOnMethods = "step24_getTSByBR")
//    public void step25_exportTSExcel() throws Exception {
//        System.out.println("▶ Step 15: Export TS Excel");
//        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
//    }
//
//
//    @Test(dependsOnMethods = "step25_exportTSExcel", description = "Step 18: Delete Test Scenario")
//    public void step26_deleteTestScenario() {
//        System.out.println("▶ Step 18: Delete Test Scenario");
//        new DeleteTestScenarioTest().deleteLastTestScenario();
//    }
//
//    @Test(dependsOnMethods = "step26_deleteTestScenario", description = "Step 19: Add Test Scenario")
//    public void step27_addTestScenario() {
//        System.out.println("▶ Step 19: Add Test Scenario");
//        new AddTestScenarioTest().addTestScenarioApiTest();
//    }
//
//    @Test(dependsOnMethods = "step27_addTestScenario", description = "Step 20: Update Test Scenario")
//    public void step28_updateTestScenario() {
//        System.out.println("▶ Step 20: Update Test Scenario");
//        new UpdateTestScenarioTest().updateTestScenarioApiTest();
//    }
//
//    @Test(dependsOnMethods = "step28_updateTestScenario")
//    public void step29_generateTC() {
//        System.out.println("▶ Step 21: Generate Test Cases (TC)");
//        new GenerateTCTest().generateTCForSelectedTS();
//    }
//
//    @Test(dependsOnMethods = "step29_generateTC")
//    public void step30_waitForTCCompletion() throws InterruptedException {
//        System.out.println("▶ Step 22: Wait for TC Generation");
//        new GetGenerationTcStatus().waitUntilAllCompletedForTC();
//    }
//    @Test(dependsOnMethods = "step30_waitForTCCompletion")
//    public void step31_exportTC() {
//        System.out.println("▶ Step 18: Export TC Excel");
//        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
//    }
//
//    @Test(dependsOnMethods = "step31_exportTC", description = "Step 23: Get Test Case Summary for TS")
//    public void step32_getTestCaseSummaryForTS() {
//        System.out.println("▶ Step 23: Get Test Case Summary for TS");
//        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
//    }
//
//    @Test(dependsOnMethods = "step32_getTestCaseSummaryForTS", description = "Step 24: Get Test Case With Steps")
//    public void step33_getTestCaseWithSteps() {
//        System.out.println("▶ Step 24: Get Test Case With Steps");
//        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
//    }
//    @Test(dependsOnMethods = "step33_getTestCaseWithSteps")
//    public void step34_addTestCaseStep() {
//        System.out.println("▶ Step 21: Add Test Case Step");
//        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
//    }
//
//    @Test(dependsOnMethods = "step34_addTestCaseStep")
//    public void step35_updateTestCaseStep() {
//        System.out.println("▶ Step 22: Update Test Case Step");
//        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
//    }
//
//    @Test(dependsOnMethods = "step35_updateTestCaseStep")
//    public void step36_deleteTestCaseStep() {
//        System.out.println("▶ Step 23: Delete Test Case Step");
//        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
//    }
//
//
//
//    @Test(
//            dependsOnMethods = "step36_deleteTestCaseStep",
//            description = "Step 25: Update Test Case Step Order"
//    )
//    public void step37_updateTestCaseStepOrder() {
//        System.out.println("▶ Step 25: Update Test Case Step Order");
//        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
//    }
//
//    @Test(dependsOnMethods = "step37_updateTestCaseStepOrder")
//    public void step38_updateTestCase() {
//        System.out.println("▶ Step 26: Update Test Case");
//        new UpdateTestCaseTest().updateTestCaseApiTest();
//    }
//
//    @Test(dependsOnMethods = "step38_updateTestCase")
//    public void step39_addTestCase() {
//        System.out.println("▶ Step 27: Add Test Case");
//        new AddTestCaseTest().addTestCaseApiTest();
//    }
//
//    @Test(dependsOnMethods = "step39_addTestCase")
//    public void step40_deleteTestCase() {
//        System.out.println("▶ Step 28: Delete Test Case");
//        new DeleteTestCaseTest().deleteLastTestCase();
//    }
//
//    @Test(
//            dependsOnMethods = "step40_deleteTestCase",
//            description = "Step 29: Generate Automation Code for Test Cases"
//    )
//    public void step41_generateAutomationCode() {
//        System.out.println("▶ Step 29: Generate Automation Code");
//        new GenerateATSTest().generateAtsApiTest();
//    }
//    }