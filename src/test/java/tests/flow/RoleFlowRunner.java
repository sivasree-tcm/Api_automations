package tests.flow;

import org.testng.annotations.Test;
import utils.ProjectStore;
import utils.RoleStore;

public class RoleFlowRunner {

    @Test
    public void step1_getProjects() {
        System.out.println("▶ Step 1: Get Projects");

        ProjectStore.clear();
        new tests.project.GetProjectsTest().fetchProjects();
    }

    @Test(dependsOnMethods = "step1_getProjects")
    public void step2_createRoles() {
        System.out.println("▶ Step 2: Create Roles");

        RoleStore.clear();
        new tests.roles.CreateRoleTest().createRolesForAllProjects();
    }

    @Test(dependsOnMethods = "step2_createRoles")
    public void step3_getRoles() {
        System.out.println("▶ Step 3: Get Roles");

        new tests.roles.GetRolesTest().getRolesForAllProjects();
    }

    @Test(dependsOnMethods = "step3_getRoles")
    public void step4_editRoles() {
        System.out.println("▶ Step 4: Edit Roles");

        new tests.roles.EditRoleTest().editRoles();
    }

    @Test(dependsOnMethods = "step4_editRoles")
    public void step5_disableUsersByRole() {
        System.out.println("▶ Step 5: Disable Users By Role");

        new tests.roles.DisableUsersByRoleTest().disableUsersByRole();
    }

    @Test(dependsOnMethods = "step5_disableUsersByRole")
    public void step6_deleteRoles() {
        System.out.println("▶ Step 6: Delete Roles");

        new tests.roles.DeleteRoleTest().deleteRoles();
    }
}
