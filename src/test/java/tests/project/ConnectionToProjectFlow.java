package tests.project;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.DbConfigTest;
import tests.connection.SaveConnectionTest;
import tests.connection.saveconnectionflow;
import tests.project.ProjectTest;

public class ConnectionToProjectFlow extends BaseTest {

    @Test(description = "Step 1: Save Connection and Capture ID")
    public void step1_saveConnection() {
        new saveconnectionflow().saveConnectionTest();
    }

    @Test(dependsOnMethods = "step1_saveConnection", description = "Step 2: Create Project using Captured ID")
    public void step2_createProject() {
        new createprojectflow().projectApiTest();
    }
    @Test(dependsOnMethods = "step2_createProject", description = "Flow Step 3: Configure Database")
    public void step3_AddDbConfig() {
        new DbConfigTest().addDbInfoTest();
    }
}