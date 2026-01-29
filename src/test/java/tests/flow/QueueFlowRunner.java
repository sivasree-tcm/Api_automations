//package tests.flow;
//
//import org.testng.annotations.Test;
//import utils.GenerationQueueStore;
//import utils.ProjectStore;
//import utils.ProjectUserStore;
//
//public class QueueFlowRunner {
//
//    @Test
//    public void step1_getProjects() {
//        System.out.println("▶ Step 1: Get Projects");
//
//        ProjectStore.clear();
//        new tests.project.GetProjectsTest().fetchProjects();
//    }
//
//    @Test(dependsOnMethods = "step1_getProjects")
//    public void step2_getProjectUsers() {
//        System.out.println("▶ Step 2: Get Project Users");
//
//        ProjectUserStore.clear();
//        new tests.project.GetProjectUsersTest().fetchProjectUsers();
//    }
//
//    @Test(dependsOnMethods = "step2_getProjectUsers")
//    public void step3_getGenerationQueue() {
//        System.out.println("▶ Step 3: Get Generation Queue");
//
//        GenerationQueueStore.clear();
//        new tests.queue.GetGenerationQueueTest().fetchGenerationQueue();
//    }
//
//    @Test(dependsOnMethods = "step3_getGenerationQueue")
//    public void step4_deleteGenerationQueue() {
//        System.out.println("▶ Step 4: Delete Generation Queue");
//
//        new tests.queue.DeleteGenerationQueueTest().deleteGenerationQueue();
//    }
//}
