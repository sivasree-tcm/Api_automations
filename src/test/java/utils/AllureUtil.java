package utils;

import io.qameta.allure.Allure;

public class AllureUtil {

    public static void attachJson(String title, String content) {
        Allure.addAttachment(title, "application/json", content);
    }

    public static void attachText(String title, String content) {
        Allure.addAttachment(title, "text/plain", content);
    }
}
