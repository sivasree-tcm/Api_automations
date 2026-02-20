package tests.flow;

import org.testng.annotations.Test;
import tests.br.UploadBusinessRequirementTest;
import tests.export.ExportTCExcelTest;
import tests.framework.DownloadAtsFrameworkTest;
import tests.generation.*;
import tests.generation.GetGenerationStatusTest;
import tests.generation.ValidateATSGenerationPollingTest;
import tests.project.*;
import utils.*;

public class UploadBRFlow {

    @Test
    public void step1_getProjects() {
        ProjectStore.clear();
        GeneratedTSStore.clear();
        TestScenarioStore.clear();
        TestCaseStore.clear();
        ATSStore.clear();
        BusinessRequirementStore.clear();
        new tests.project.GetProjectsTest().fetchProjects();
    }

    @Test(dependsOnMethods = "step1_getProjects")
    public void step2_getProjectDetails() {
        System.out.println("▶ Step 2: Get Project Details");
        new GetProjectDetailsTest().fetchProjectDetails(144);
    }

    @Test(dependsOnMethods = "step2_getProjectDetails")
    public void step3_getBRs() {
        System.out.println("▶ Step 3: Get BRs");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step3_getBRs")
    public void step4_deleteBRs() {
        System.out.println("▶ Step 4: Delete BRs");
        new tests.br.DeleteBusinessRequirementTest().deleteBRs();
    }

    @Test(dependsOnMethods = "step4_deleteBRs")
    public void step5_uploadBusinessRequirement() {
        System.out.println("▶ Step 5: Upload BRs");
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step5_uploadBusinessRequirement")
    public void step6_getBRs() {
        System.out.println("▶ Step 6: Get BRs");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

//    @Test(dependsOnMethods = "step9_getBRs")
//    public void step10_uploadFilesForBR() {
//        System.out.println("▶ Step 10: Image Upload Files for BR");
//        new tests.uploadFiles.UploadFilesForBRTest().uploadImageForALLBRs();
//    }

    @Test(dependsOnMethods = "step6_getBRs")
    public void step7_exportBRExcel() throws Exception {
        System.out.println("▶ Step 11: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step7_exportBRExcel")
    public void step8_generateTS() {
        System.out.println("▶ Step 8: Generate Test Scenarios");
        new tests.generation.GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step8_generateTS")
    public void step9_getGenerationStatus() {
        System.out.println("▶ Step 9: Get Generation Status");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step9_getGenerationStatus")
    public void step10_getTSByBR() {
        System.out.println("▶ Step 10: Get TS for each BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step10_getTSByBR")
    public void step11_exportTSExcel() throws Exception {
        System.out.println("▶ Step 11: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step11_exportTSExcel")
    public void step12_generateTC() {
        System.out.println("▶ Step 12: Generate Test Cases");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step12_generateTC")
    public void step13_getGenerationStatus() {
        System.out.println("▶ Step 13: Wait for TC Generation");
        new GetGenerationTCStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step13_getGenerationStatus")
    public void step14_exportTC() {
        System.out.println("▶ Step 14: Export TC Excel");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step14_exportTC")
    public void step15_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 15: Get Test Case Summary");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step15_getTestCaseSummaryForTS")
    public void step16_getTestCaseWithSteps() {
        System.out.println("▶ Step 16: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    // ✅ NEWLY INSERTED STEPS

    @Test(dependsOnMethods = "step16_getTestCaseWithSteps")
    public void step17_addTestCaseStep() {
        System.out.println("▶ Step 17: Add Test Case Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step17_addTestCaseStep")
    public void step18_updateTestCaseStep() {
        System.out.println("▶ Step 18: Update Test Case Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step18_updateTestCaseStep")
    public void step19_deleteTestCaseStep() {
        System.out.println("▶ Step 19: Delete Test Case Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }

    @Test(dependsOnMethods = "step19_deleteTestCaseStep")
    public void step20_updateTestCaseStepOrder() {
        System.out.println("▶ Step 20: Update Test Case Step Order");
        new UpdateTestCaseStepOrderTest().updateTestCaseStepOrderApiTest();
    }

    @Test(dependsOnMethods = "step20_updateTestCaseStepOrder")
    public void step21_updateTestCase() {
        System.out.println("▶ Step 21: Update Test Case");
        new UpdateTestCaseTest().updateTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step21_updateTestCase")
    public void step22_addTestCase() {
        System.out.println("▶ Step 22: Add Test Case");
        new AddTestCaseTest().addTestCaseApiTest();
    }

    @Test(dependsOnMethods = "step22_addTestCase")
    public void step23_deleteTestCase() {
        System.out.println("▶ Step 23: Delete Test Case");
        new DeleteTestCaseTest().deleteLastTestCase();
    }

    @Test(dependsOnMethods = "step23_deleteTestCase")
    public void step24_generateAutomationCode() {
        System.out.println("▶ Step 24: Generate Automation Code");
        new GenerateATSTest().generateAtsApiTest();
    }

    @Test(dependsOnMethods = "step24_generateAutomationCode")
    public void step25_checkATSStatus() {
        System.out.println("▶ Step 25: Check the ATS Status");
        new ValidateATSGenerationPollingTest().validateATSGenerationWithPolling();
    }

    @Test(dependsOnMethods = "step25_checkATSStatus")
    public void step26_downloadATSFramework() {
        System.out.println("▶ Step 26: Download ATS Framework");
        new DownloadAtsFrameworkTest().downloadAtsFramework();
        ProjectStore.clear();
        GeneratedTSStore.clear();
        TestScenarioStore.clear();
        TestCaseStore.clear();
        ATSStore.clear();
        BusinessRequirementStore.clear();
    }

}
