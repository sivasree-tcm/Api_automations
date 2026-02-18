package tests.flow;

import org.testng.annotations.Test;
import tests.br.UploadBusinessRequirementTest;
import tests.export.ExportTCExcelTest;
import tests.generation.GenerateTCTest;
import tests.generation.GetGenerationStatusTest;
import tests.generation.GetGenerationTcStatus;
import tests.generation.GetTSByBRTest;
import tests.project.GetProjectDetailsTest;
import tests.project.GetTestCaseSummaryForTSTest;
import tests.project.GetTestCaseWithStepsTest;
import tests.sprints.GetAzureSprintsTest;
import tests.sprints.GetUserStoriesTest;
import tests.sprints.ImportAzureUserStoriesTest;
import utils.*;

public class QuickGenerationFlow {

    @Test
    public void step1_getProjects() {
        ProjectStore.clear();
        new tests.project.GetProjectsTest().fetchProjects();
    }

    @Test(dependsOnMethods = "step1_getProjects")
    public void step2_getProjectDetails() {
        System.out.println("▶ Step 2: Get Project Details");
        new GetProjectDetailsTest().fetchProjectDetails(99);
    }

//    @Test(dependsOnMethods = "step2_getProjectDetails")
//    public void step3_getBRs() {
//        System.out.println("▶ Step 3: Get BRs");
//        BusinessRequirementStore.clear();
//        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
//    }
//
//    @Test(dependsOnMethods = "step3_getBRs")
//    public void step5_deleteBRs() {
//        System.out.println("▶ Step 5: Delete BRs");
//        new tests.br.DeleteBusinessRequirementTest().deleteBRs();
//    }

//
//    @Test(dependsOnMethods = "step5_deleteBRs")
//    public void step6_getAzureSprints() {
//        System.out.println("▶ Step 6: Get Azure DevOps Sprints");
//        SprintStore.clear();
//        new GetAzureSprintsTest().getAzureDevOpsSprints();
//    }
//
//    @Test(dependsOnMethods = "step6_getAzureSprints")
//    public void step7_getUserStories() {
//        System.out.println("▶ Step 7: Get User Stories");
//        new GetUserStoriesTest().getUserStories();
//    }

//    @Test(dependsOnMethods = "step7_getUserStories")
//    public void step8_importAzureUserStories() {
//        System.out.println("▶ Step 8: Import Azure User Stories");
//        new ImportAzureUserStoriesTest().importAzureUserStories();
//    }

    @Test(dependsOnMethods = "step2_getProjectDetails")
    public void step9_getBRs() {
        System.out.println("▶ Step 9: Get BRs");
        BusinessRequirementStore.clear();
        new UploadBusinessRequirementTest().uploadBusinessRequirementTest();
    }

    @Test(dependsOnMethods = "step9_getBRs")
    public void step10_uploadFilesForBR() {
        System.out.println("▶ Step 10: Image Upload Files for BR");
        new tests.uploadFiles.UploadFilesForBRTest().uploadImageForALLBRs();
    }

    @Test(dependsOnMethods = "step10_uploadFilesForBR")
    public void step11_exportBRExcel() throws Exception {
        System.out.println("▶ Step 11: Export BR Excel");
        new tests.export.ExportBRExcelTest().exportBRExcelAndValidate();
    }

    @Test(dependsOnMethods = "step11_exportBRExcel")
    public void step12_generateTS() {
        System.out.println("▶ Step 12: Generate Test Scenarios");
        new tests.generation.GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step12_generateTS")
    public void step13_getGenerationStatus() {
        System.out.println("▶ Step 13: Get Generation Status");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }

    @Test(dependsOnMethods = "step13_getGenerationStatus")
    public void step14_getTSByBR() {
        System.out.println("▶ Step 14: Get TS for each BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step14_getTSByBR")
    public void step15_exportTSExcel() throws Exception {
        System.out.println("▶ Step 15: Export TS Excel");
        new tests.export.ExportTSExcelTest().exportTSExcelForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step15_exportTSExcel")
    public void step16_generateTC() {
        System.out.println("▶ Step 16: Generate Test Cases");
        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step16_generateTC")
    public void step17_getGenerationStatus() {
        System.out.println("▶ Step 17: Wait for TC Generation");
        new GetGenerationTcStatus().waitUntilAllCompletedForTC();
    }

    @Test(dependsOnMethods = "step17_getGenerationStatus")
    public void step18_exportTC() {
        System.out.println("▶ Step 18: Export TC Excel");
        new ExportTCExcelTest().exportTCExcelForGeneratedTS();
    }

    @Test(dependsOnMethods = "step18_exportTC")
    public void step19_getTestCaseSummaryForTS() {
        System.out.println("▶ Step 19: Get Test Case Summary");
        new GetTestCaseSummaryForTSTest().getTestCaseSummaryForTS();
    }

    @Test(dependsOnMethods = "step19_getTestCaseSummaryForTS")
    public void step20_getTestCaseWithSteps() {
        System.out.println("▶ Step 20: Get Test Case With Steps");
        new GetTestCaseWithStepsTest().getTestCaseWithStepsApiTest();
    }

    // ✅ NEWLY INSERTED STEPS

    @Test(dependsOnMethods = "step20_getTestCaseWithSteps")
    public void step21_addTestCaseStep() {
        System.out.println("▶ Step 21: Add Test Case Step");
        new tests.testCase.AddTestCaseStepTest().addTestCaseStep();
    }

    @Test(dependsOnMethods = "step21_addTestCaseStep")
    public void step22_updateTestCaseStep() {
        System.out.println("▶ Step 22: Update Test Case Step");
        new tests.testCase.UpdateTestCaseStepTest().updateTestCaseStep();
    }

    @Test(dependsOnMethods = "step22_updateTestCaseStep")
    public void step23_deleteTestCaseStep() {
        System.out.println("▶ Step 23: Delete Test Case Step");
        new tests.testCase.DeleteTestCaseStepTest().deleteTestCaseStep();
    }
}