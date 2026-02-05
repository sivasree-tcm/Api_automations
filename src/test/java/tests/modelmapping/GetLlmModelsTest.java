package tests.modelmapping;

import api.modelmapping.GetLlmModelsApi;
import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.LlmModelSelector;
import utils.TokenUtil;

public class GetLlmModelsTest extends BaseTest {

    @Test
    public void fetchAndStoreModels() {

        int userId = TokenUtil.getUserId("SUPER_ADMIN");

        Response response = GetLlmModelsApi.getModels(
                userId,
                "SUPER_ADMIN",
                "VALID"
        );

        // 👇 Stores BOTH multimodal + chat model IDs
        LlmModelSelector.selectAndStoreModels(response);
    }
}
