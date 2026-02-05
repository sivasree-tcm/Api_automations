package utils;

import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class LlmModelSelector {

    public static void selectAndStoreModels(Response response) {

        List<Map<String, Object>> models =
                response.jsonPath().getList("$");

        for (Map<String, Object> model : models) {

            String modelName = String.valueOf(model.get("model_name"));
            String modelType = String.valueOf(model.get("model_type"));
            Integer modelId = (Integer) model.get("model_id");

            // 🔹 MULTIMODAL
            if (modelType.equalsIgnoreCase("multimodal")
                    && modelName.contains("claude")) {
                ModelStore.setMultimodalModelId(modelId);
            }

            // 🔹 CHAT
            if (modelType.equalsIgnoreCase("chat")
                    && modelName.contains("claude")) {
                ModelStore.setChatModelId(modelId);
            }
        }

        // Safety check
        ModelStore.getMultimodalModelId();
        ModelStore.getChatModelId();
    }
}
