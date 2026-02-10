package tests.project;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.DbConfigTest;
import tests.connection.saveconnectionflow;
import tests.generation.GenerateTCTest;
import tests.generation.GetGenerationTcStatus;
import tests.generation.GetTSByBRTest;
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
import tests.generation.GetGenerationStatusTest;
import tests.project.GenerateTSTest;
import tests.generation.AddTestScenarioTest;
import tests.generation.UpdateTestScenarioTest;

public class ConnectionToProjectFlow extends BaseTest {

    @Test
    public void step1_saveConnection() {
        new saveconnectionflow().saveConnectionTest();
    }

    @Test(dependsOnMethods = "step1_saveConnection")
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
        new GetProjectProviderConfigTest()
                .getProjectProviderConfigApiTest();
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

    @Test(dependsOnMethods = "step12_mapPrompt")
    public void step13_uploadBusinessRequirement() {
        new UploadBusinessRequirementTest()
                .uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step13_uploadBusinessRequirement")
    public void step14_getBRs() {
        System.out.println("▶ Step 14: Get Business Requirements");
        new GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step14_getBRs")
    public void step15_generateTS() {
        System.out.println("▶ Step 15: Generate Test Scenarios (TS)");
        new GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step15_generateTS")
    public void step16_waitForTSGeneration() {
        System.out.println("▶ Step 16: Wait for TS Generation");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step16_waitForTSGeneration")
    public void step17_getTSByBR() {
        System.out.println("▶ Step 17: Get TS by BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    // ✅ NEW STEP 18: Add Test Scenario
    @Test(
            dependsOnMethods = "step17_getTSByBR",
            description = "Step 18: Add Test Scenario"
    )
    public void step18_addTestScenario() {
        System.out.println("▶ Step 18: Add Test Scenario");
        new AddTestScenarioTest().addTestScenarioApiTest();
    }

    // ✅ NEW STEP 19: Update Test Scenario
    @Test(
            dependsOnMethods = "step18_addTestScenario",
            description = "Step 19: Update Test Scenario"
    )
    public void step19_updateTestScenario() {
        System.out.println("▶ Step 19: Update Test Scenario");
        new UpdateTestScenarioTest().updateTestScenarioApiTest();
    }

    @Test(dependsOnMethods = "step19_updateTestScenario")
    public void step20_generateTC() {
        System.out.println("▶ Step 20: Generate Test Cases (TC)");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step20_generateTC")
    public void step21_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 21: Wait for TC Generation");
        new GetGenerationTcStatus().waitUntilAllCompletedForTC();
    }
    @Test(
            dependsOnMethods = "step21_waitForTCCompletion",
            description = "Step 22: Get Test Case Summary for TS"
    )
    public void step22_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 22: Get Test Case Summary for TS");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(
            dependsOnMethods = "step22_getTestCaseSummaryForTS",
            description = "Step 23: Get Test Case With Steps"
    )
    public void step23_getTestCaseWithSteps() {
        System.out.println("▶ Step 23: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    @Test(dependsOnMethods = "step23_getTestCaseWithSteps")
    public void step24_updateTestCase() {
        System.out.println("▶ Step 22: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }
    @Test(dependsOnMethods = "step24_updateTestCase")
    public void step24_addpTestCase() {
        System.out.println("▶ Step 22: Update Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }





}
