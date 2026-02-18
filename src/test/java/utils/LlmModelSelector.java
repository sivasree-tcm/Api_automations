package utils;

import io.restassured.response.Response;
import java.util.List;
import java.util.Map;

public class LlmModelSelector {

    // 🔥 THIS is the exact model you want
    private static final String TARGET_MODEL_NAME =
            "bedrock - us.anthropic.claude-3-7-sonnet-20250219-v1:0";

    public static void selectAndStoreModels(Response response) {

        List<Map<String, Object>> models =
                response.jsonPath().getList("$");

        boolean chatFound = false;
        boolean multimodalFound = false;

        for (Map<String, Object> model : models) {

            Integer modelId = (Integer) model.get("model_id");
            String modelName = String.valueOf(model.get("model_name"));
            String modelType = String.valueOf(model.get("model_type"));

            // ✅ EXACT MATCH ONLY
            if (!TARGET_MODEL_NAME.equalsIgnoreCase(modelName)) {
                continue;
            }

            if ("chat".equalsIgnoreCase(modelType)) {
                ModelStore.setChatModelId(modelId);
                chatFound = true;
            }

            if ("multimodal".equalsIgnoreCase(modelType)) {
                ModelStore.setMultimodalModelId(modelId);
                multimodalFound = true;
            }
        }

        // 🚨 HARD FAIL if missing
        if (!chatFound) {
            throw new IllegalStateException("❌ Chat model not found");
        }

        if (!multimodalFound) {
            throw new IllegalStateException("❌ Multimodal model not found");
        }
    }
}
