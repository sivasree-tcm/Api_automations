//package tests.flow;
//
//import org.testng.annotations.Test;
//import utils.PromptStore;
//
//public class PromptFlowRunner {
//
//    @Test
//    public void step1_createPrompt() {
//        System.out.println("▶ Step 1: Create Prompt");
//
//        PromptStore.clear();
//        new tests.prompt.CreatePromptTest().createPromptApiTest();
//    }
//
//    @Test(dependsOnMethods = "step1_createPrompt")
//    public void step2_getPrompt() {
//        System.out.println("▶ Step 2: Get Prompt");
//
//        new tests.prompt.GetPromptTest().getPromptApiTest();
//    }
//
//    @Test(dependsOnMethods = "step2_getPrompt")
//    public void step3_updatePrompt() {
//        System.out.println("▶ Step 3: Update Prompt");
//
//        new tests.prompt.UpdatePromptTest().updatePromptApiTest();
//    }
//}
