package tests.queue;

import api.login.LoginApi;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import tests.login.LoginTest;

import java.util.List;
import java.util.Map;

public class GenerationQueueFlowTest {

    // Base URL
    private static final String BASE_URL = "https://test.cognitest.ai";
//    LoginTest loginTest = new LoginTest();


    // Token (use your existing token util if needed)
//    private static final String TOKEN = "Bearer YOUR_TOKEN_HERE";


    public void validateGenerationQueueForAllProjectsAndUsers() {

        // ================================
        // STEP 1: GET PROJECTS
        // ================================
        Response projectResponse =
                RestAssured.given()
                        .baseUri(BASE_URL)
                        .header("Authorization", "Bearer " + LoginTest.authToken)
                        .body("{\"userId\":32}")
                        .post("/api/getProjects");

        List<Map<String, Object>> projects =
                projectResponse.jsonPath().getList("data");

        System.out.println("Total Projects: " + projects.size());

        // Loop through all projects
        for (Map<String, Object> project : projects) {

            int projectId = (int) project.get("projectId");
            String projectName = project.get("projectName").toString();

            System.out.println("\nProject -> " + projectName + " (" + projectId + ")");

            // ================================
            // STEP 2: GET PROJECT USERS
            // ================================
            String userPayload = "{\n" +
                    "  \"projectId\": " + projectId + ",\n" +
                    "  \"userId\": 32\n" +
                    "}";

            Response userResponse =
                    RestAssured.given()
                            .baseUri(BASE_URL)
                            .header("Authorization", "Bearer " + LoginTest.authToken)
                            .body(userPayload)
                            .post("/api/getProjectUsers");

            List<Map<String, Object>> users =
                    userResponse.jsonPath().getList("users");

            if (users == null || users.isEmpty()) {
                System.out.println("No users found for project: " + projectName);
                continue;
            }

            // ================================
            // STEP 3: GET GENERATION QUEUE
            // ================================
            for (Map<String, Object> user : users) {

                int userIdFromApi = (int) user.get("userId");
                String userName =
                        user.get("userFirstName") + " " + user.get("userLastName");

                String queuePayload =
                        "{\n" +
                                "  \"userIdFromAPI\": " + userIdFromApi + ",\n" +
                                "  \"projectId\": " + projectId + ",\n" +
                                "  \"userId\": \"32\",\n" +
                                "  \"userName\": \"" + userName + "\",\n" +
                                "  \"projectName\": \"" + projectName + "\"\n" +
                                "}";

                Response queueResponse =
                        RestAssured.given()
                                .baseUri(BASE_URL)
                                .header("Authorization", "Bearer " + LoginTest.authToken)
                                .body(queuePayload)
                                .post("/api/getGenerationQueue");

                System.out.println(
                        "Queue Response | Project: " + projectName +
                                " | User: " + userName +
                                " | Status: " + queueResponse.getStatusCode()
                );
            }
        }
    }
}
