package tests.configuration;

import api.configuration.ConfigApi;
import io.restassured.response.Response;
import models.configuration.ConfigRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.AllureUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigTest {

    private ConfigRequest buildConfigRequest(
            boolean isSaveConfig,
            boolean isAddDbInfo
    ) {

        ConfigRequest request = new ConfigRequest();
        request.setUserId("32");
        request.setProjectId("108");

        // ---- SAVE CONFIG ----
        if (isSaveConfig) {
            request.setModel("OpenAI");
            request.setRefBR("With RefBR");
            request.setTestDataMCP("Generate Test Data with MCP");
            request.setAtsBranch("AutoGen");
            request.setAzureDevOps("Direct");
            request.setDbConfig("service account based connection");

            Map<String, Object> apiSettings = new HashMap<>();
            apiSettings.put("temperature", 0.5);
            apiSettings.put("version", "v1");

            request.setApiSettings(apiSettings);
            request.setThreshold(1);
        }

        // ---- ADD DB INFO ----
        if (isAddDbInfo) {
            Map<String, Object> env = new HashMap<>();
            env.put("environmentName", "Insurance");
            env.put("envirtonmentUrl", "http://52.91.206.42:3000/");
            env.put("db_name", "insurance_db");
            env.put("db_host", "52.91.206.42");
            env.put("db_user", "ins_user");
            env.put("db_password", "ins_password");
            env.put("port", "3306");

            request.setEnvironemntDetails(List.of(env));
        }

        return request;
    }

    @Test
    public void getConfigApi() {

        ConfigRequest request = buildConfigRequest(false, false);
        Response response = ConfigApi.getConfig(request);

        AllureUtil.attachText("Request URL", "/api/getConfig");
        AllureUtil.attachJson("Request", request.toString());
        AllureUtil.attachJson("Response", response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.getBody().asString());
    }

    @Test
    public void saveConfigApi() {

        ConfigRequest request = buildConfigRequest(true, false);
        Response response = ConfigApi.saveConfig(request);

        AllureUtil.attachText("Request URL", "/api/saveConfig");
        AllureUtil.attachJson("Request", request.toString());
        AllureUtil.attachJson("Response", response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.getBody().asString());
    }

    @Test
    public void addDbInfoApi() {

        ConfigRequest request = buildConfigRequest(false, true);
        Response response = ConfigApi.addDbInfo(request);

        AllureUtil.attachText("Request URL", "/api/addDbInfo");
        AllureUtil.attachJson("Request", request.toString());
        AllureUtil.attachJson("Response", response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println(response.getBody().asString());
    }

}
