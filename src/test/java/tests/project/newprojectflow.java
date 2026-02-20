package tests.project;

import api.generation.GenerateATSApi;
import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.DbConfigTest;
import tests.connection.saveconnectionflow;
import tests.export.ExportTCExcelTest;
import tests.generation.*;
import tests.generation.GetGenerationStatusTest;
import tests.project.createprojectflow;
import tests.modelmapping.GetLlmModelsTest;
import tests.modelmapping.MapLlmToProjectTest;
import tests.project.GetLlmCredentialsTest;
import tests.project.MapCredentialToProjectTest;
import tests.project.GetProjectProviderConfigTest;
import tests.project.CreateRole;
import tests.project.AddUpdateProjecTest;
import tests.project.createprompt;
import tests.project.mapprompt;
import tests.br.UploadBusinessRequirementTest;
import tests.project.GetBusinessRequirementTest;
import tests.project.GenerateTSTest;
import utils.BusinessRequirementStore;
import utils.SprintStore;

public class newprojectflow extends BaseTest {

//    @Test
//    public void step1_saveConnection() {
//        new saveconnectionflow().saveConnectionTest();
//    }
//
//    @Test(dependsOnMethods = "step1_saveConnection")
    @Test
    public void step2_createProject() {
        new createprojectflow().projectApiTest();
    }

    @Test(dependsOnMethods = "step2_createProject")
    public void step3_AddDbConfig() {
        new DbConfigTest().addDbInfoTest();
    }

    @Test(dependsOnMethods = "step3_AddDbConfig")
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
    public void step9_createRole() {
        new CreateRole().createRoleTest();
    }

    @Test(dependsOnMethods = "step9_createRole")
    public void step10_addUser() {
        new AddUpdateProjecTest().addUpdateProjectUserApiTest();
    }

    @Test(dependsOnMethods = "step10_addUser")
    public void step11_createPrompt() {
        new createprompt().createPromptApiTest();
    }

    @Test(dependsOnMethods = "step11_createPrompt")
    public void step12_mapPrompt() {
        new mapprompt().mapPromptApiTest();
    }

    // Azure Steps 13, 14, 15 Removed. Step 16 now depends on 12.

    @Test(dependsOnMethods = "step12_mapPrompt")
    public void step13_getBRs() {
        System.out.println("▶ Step 13: Get BRs");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step13_getBRs")
    public void step14_uploadBusinessRequirement() {
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }



    @Test(dependsOnMethods = "step14_uploadBusinessRequirement")
    public void step16_exportBRExcel() throws Exception {
        System.out.println("▶ Step 16: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step16_exportBRExcel")
    public void step17_generateTS() {
        System.out.println("▶ Step 17: Generate Test Scenarios");
        new tests.generation.GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step17_generateTS")
    public void step18_getBRs() {
        System.out.println("▶ Step 18: Get Business Requirements");
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step18_getBRs")
    public void step19_generateTS() {
        System.out.println("▶ Step 19: Generate Test Scenarios (TS)");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step19_generateTS")
    public void step20_waitForTSGeneration() {
        System.out.println("▶ Step 20: Wait for TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step20_waitForTSGeneration")
    public void step21_getTSByBR() {
        System.out.println("▶ Step 21: Get TS by BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step21_getTSByBR")
    public void step22_exportTSExcel() throws Exception {
        System.out.println("▶ Step 22: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step22_exportTSExcel")
    public void step23_deleteTestScenario() {
        System.out.println("▶ Step 23: Delete Test Scenario");
        new DeleteTestScenarioTest().deleteLastTestScenario();
    }

    @Test(dependsOnMethods = "step23_deleteTestScenario")
    public void step24_addTestScenario() {
        System.out.println("▶ Step 24: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step24_addTestScenario")
    public void step25_updateTestScenario() {
        System.out.println("▶ Step 25: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step25_updateTestScenario")
    public void step26_generateTC() {
        System.out.println("▶ Step 26: Generate Test Cases (TC)");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step26_generateTC")
    public void step27_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 27: Wait for TC Generation");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step27_waitForTCCompletion")
    public void step28_exportTC() {
        System.out.println("▶ Step 28: Export TC Excel");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step28_exportTC")
    public void step29_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 29: Get Test Case Summary for TS");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step29_getTestCaseSummaryForTS")
    public void step30_getTestCaseWithSteps() {
        System.out.println("▶ Step 30: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step30_getTestCaseWithSteps")
    public void step31_addTestCaseStep() {
        System.out.println("▶ Step 31: Add Test Case Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step31_addTestCaseStep")
    public void step32_updateTestCaseStep() {
        System.out.println("▶ Step 32: Update Test Case Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step32_updateTestCaseStep")
    public void step33_deleteTestCaseStep() {
        System.out.println("▶ Step 33: Delete Test Case Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step33_deleteTestCaseStep")
    public void step34_updateTestCaseStepOrder() {
        System.out.println("▶ Step 34: Update Test Case Step Order");
        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
    }

    @Test(dependsOnMethods = "step34_updateTestCaseStepOrder")
    public void step35_updateTestCase() {
        System.out.println("▶ Step 35: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step35_updateTestCase")
    public void step36_addTestCase() {
        System.out.println("▶ Step 36: Add Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step36_addTestCase")
    public void step37_deleteTestCase() {
        System.out.println("▶ Step 37: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step37_deleteTestCase")
    public void step38_generateAutomationCode() {
        System.out.println("▶ Step 38: Generate Automation Code");
        new GenerateATSTest().generateAtsApiTest();
    }

}