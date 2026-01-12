package utils;

import io.qameta.allure.Allure;

public class AllureUtil {

    public static void attachJson(String title, String json) {
        Allure.addAttachment(title, "application/json", json);
    }

    public static void attachText(String title, String text) {
        Allure.addAttachment(title, "text/plain", text);
    }
}
