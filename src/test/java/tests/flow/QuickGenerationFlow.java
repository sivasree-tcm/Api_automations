package tests.flow;

import org.testng.annotations.Test;
import tests.generation.GenerateTCTest;
import tests.generation.GetGenerationStatusTest;
import tests.generation.GetGenerationTcStatus;
import tests.generation.GetTSByBRTest;
//import tests.generation.WaitForTCCompletionTest;
import tests.project.GetProjectDetailsTest;
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

        // Only for required project
        new GetProjectDetailsTest().fetchProjectDetails(1164);
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
    public void step5_getAzureSprints() {
        System.out.println("▶ Step 5: Get Azure DevOps Sprints");
        SprintStore.clear();
        new GetAzureSprintsTest().getAzureDevOpsSprints();
    }
    @Test(dependsOnMethods = "step5_getAzureSprints")
    public void step6_getUserStories() {
        System.out.println("▶ Step 6: Get User Stories");
        new GetUserStoriesTest().getUserStories();
    }


    @Test(dependsOnMethods = "step6_getUserStories")
    public void step7_importAzureUserStories() {
        System.out.println("▶ Step 7: Import Azure User Stories");

        new ImportAzureUserStoriesTest().importAzureUserStories();
    }

    @Test(dependsOnMethods="step7_importAzureUserStories")
    public void step8_getBRs() {
        System.out.println("▶ Step 8: Get BRs");
        BusinessRequirementStore.clear();
        new tests.br.GetBusinessRequirementTest().fetchBRsForProject();
    }

    @Test(dependsOnMethods = "step8_getBRs")
    public void step9_generateTS() {
        System.out.println("▶ Step 9: Generate Test Scenarios");

        new tests.generation.GenerateTSTest().generateTSForBR();
    }

    @Test(dependsOnMethods = "step9_generateTS")
    public void step10_getGenerattionStatus() {
        System.out.println("▶ Step 9: Generate Test Scenarios");


       new GetGenerationStatusTest().waitUntilAllCompleted();
    }
    @Test(dependsOnMethods = "step10_getGenerattionStatus")
    public void
    step11_getTSByBR() {
        System.out.println("▶ Step 10: Get TS for each BR");
        new GetGenerationStatusTest().waitUntilAllCompleted();
    }
    @Test(dependsOnMethods = "step11_getTSByBR")
    public void step12_getTSByBR() {
        System.out.println("▶ Step 10: Get TS for each BR");
        new GetTSByBRTest().getTestScenariosForGeneratedBRs();
    }

    @Test(dependsOnMethods = "step12_getTSByBR")
    public void step12_generateTC() {
        System.out.println("▶ Step 11: Generate Test Cases (TC)");

        new GenerateTCTest().generateTCForSelectedTS();
    }

    @Test(dependsOnMethods = "step12_generateTC")
    public void step13_waitForTCCompletion() throws InterruptedException {
        System.out.println("▶ Step 12: Wait for TC Generation to Complete");
        new GetGenerationTcStatus().waitUntilAllCompletedForTC();

    }

}
