package tests.project;

import base.BaseTest;
import org.testng.annotations.Test;
import tests.connection.DbConfigTest;
import tests.connection.saveconnectionflow;
import tests.project.createprojectflow;
import tests.modelmapping.GetLlmModelsTest;
import tests.modelmapping.MapLlmToProjectTest;

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
}
