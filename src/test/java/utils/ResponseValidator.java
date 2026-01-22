package utils;

import io.restassured.response.Response;

import java.util.Collections;
import java.util.List;

public class ResponseValidator {

    public static boolean isJson(Response response) {
        return response != null
                && response.getContentType() != null
                && response.getContentType().contains("application/json");
    }

    public static boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }

    public static List<Integer> safeExtractIdList(Response response, String path) {

        if (!isSuccess(response)) {
            System.out.println("⚠ Skipping JSON parse. Status = "
                    + response.getStatusCode());
            return Collections.emptyList();
        }

        if (!isJson(response)) {
            System.out.println("⚠ Response is not JSON: "
                    + response.getContentType());
            return Collections.emptyList();
        }

        try {
            return response.jsonPath().getList(path);
        } catch (Exception e) {
            System.out.println("⚠ JSON parsing failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
