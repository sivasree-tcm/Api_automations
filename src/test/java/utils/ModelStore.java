package utils;

public class ModelStore {

    private static Integer chatModelId;
    private static Integer multimodalModelId;

    public static void setChatModelId(Integer id) {
        chatModelId = id;
        System.out.println("✅ Stored CHAT modelId = " + id);
    }

    public static void setMultimodalModelId(Integer id) {
        multimodalModelId = id;
        System.out.println("✅ Stored MULTIMODAL modelId = " + id);
    }

    public static Integer getChatModelId() {
        if (chatModelId == null) {
            throw new IllegalStateException("❌ Chat modelId not set");
        }
        return chatModelId;
    }

    public static Integer getMultimodalModelId() {
        if (multimodalModelId == null) {
            throw new IllegalStateException("❌ Multimodal modelId not set");
        }
        return multimodalModelId;
    }
}
