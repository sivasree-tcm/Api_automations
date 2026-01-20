package api.project;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ProjectApi {

    // CREATE PROJECT
    public static Response createProject(Object request, String role) {

        System.out.println("Executing as role: " + role);

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken())
                .body(request)
                .post("/api/createProject");
    }



    // ✅ GET MY PROJECTS (CONFIRMED CONTRACT)
    public static Response getMyProjects() {

        String payload = "{ \"userId\": \"" + TokenUtil.getUserId() + "\" }";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken())
                .body(payload)
                .when()
                .post("/api/getMyProjects");
    }
    public static Response getProjectDetailsByProjectId(int projectId) {

        String payload = "{"
                + "\"userId\":\"" + TokenUtil.getUserId() + "\","
                + "\"projectId\":\"" + projectId + "\""
                + "}";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken())
                .body(payload)
                .when()
                .post("/api/getProjectDetailsByProjectId");
    }
    public static Response getProjects() {

        String payload = "{"
                + "\"userId\":\"" + TokenUtil.getUserId() + "\","
                + "\"orgId\":\"1\""
                + "}";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken())
                .body(payload)
                .when()
                .post("/api/getProjects");
    }
    public static Response getMyProjects(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        // ✅ Authorization handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken());
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/getMyProjects");
    }

}
