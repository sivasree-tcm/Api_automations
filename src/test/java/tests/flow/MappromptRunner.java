//package tests.flow;
//
//import org.testng.annotations.Test;
//import tests.project.GetProjectsTest;
//import tests.prompt.GetPromptTest;
//import tests.prompt.MapPromptTest;
//import utils.ProjectStore;
//import utils.PromptStore;
//
//public class MappromptRunner {
//
//    @Test
//    public void step1_getProjects() {
//        System.out.println("▶ Step 1: Get Projects");
//        ProjectStore.clear();
//        new GetProjectsTest().fetchProjects();
//    }
//
//    @Test(dependsOnMethods = "step1_getProjects")
//    public void step2_getPrompt() {
//        System.out.println("▶ Step 2: Get Prompt");
//        PromptStore.clear();
//        new GetPromptTest().getPromptApiTest();
//    }
//
//    @Test(dependsOnMethods = "step2_getPrompt")
//    public void step3_mapPrompt() {
//        System.out.println("▶ Step 3: Map Prompt");
//        new MapPromptTest().mapPromptApiTest();
//    }
//}
