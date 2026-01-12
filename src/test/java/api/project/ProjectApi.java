package api.project;

import io.restassured.response.Response;
import models.project.ProjectRequest;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ProjectApi {

    public static Response createProject(ProjectRequest request) {

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken())
                .body(request)
                .when()
                .post("/api/createProject");
    }
}
