package api.project;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tests.roles.UserRole;
import utils.TokenUtil;

import static io.restassured.RestAssured.given;

public class ProjectApi {

    // ✅ CREATE PROJECT - WITH STRING ROLE (HANDLES SPECIAL CASES)
    public static Response createProject(Object request, String role) {

        System.out.println("Executing as role: " + role);

        // ✅ Handle NO_AUTH
        if ("NO_AUTH".equalsIgnoreCase(role)) {
            return given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .post("/api/createProject");
        }

        // ✅ Handle INVALID_TOKEN
        if ("INVALID_TOKEN".equalsIgnoreCase(role)) {
            return given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer invalid_token_12345")
                    .body(request)
                    .post("/api/createProject");
        }

        // ✅ Handle INVALID_CONTENT_TYPE
        if ("INVALID_CONTENT_TYPE".equalsIgnoreCase(role)) {
            return given()
                    .contentType(ContentType.TEXT)  // Send as text/plain instead of JSON
                    .header("Authorization", TokenUtil.getToken(UserRole.SUPER_ADMIN))
                    .body(request)
                    .post("/api/createProject");
        }

        // ✅ Normal flow - Convert String role to UserRole enum
        UserRole userRole = UserRole.valueOf(role.toUpperCase().replace("-", "_").replace(" ", "_"));

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(userRole))
                .body(request)
                .post("/api/createProject");
    }

    // ✅ CREATE PROJECT - WITH UserRole ENUM (RECOMMENDED)
    public static Response createProject(Object request, UserRole role) {

        System.out.println("Executing as role: " + role);

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", TokenUtil.getToken(role))
                .body(request)
                .post("/api/createProject");
    }

    // ✅ CREATE PROJECT - DEFAULT (SUPER_ADMIN)
    public static Response createProject(Object request) {
        return createProject(request, UserRole.SUPER_ADMIN);
    }

    // ✅ CREATE PROJECT - NO AUTH (for testing unauthorized access)
    public static Response createProjectNoAuth(Object request) {

        System.out.println("Executing WITHOUT authentication");

        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/createProject");
    }

    // ✅ CREATE PROJECT - INVALID TOKEN (for testing invalid token)
    public static Response createProjectInvalidToken(Object request) {

        System.out.println("Executing with INVALID token");

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token_12345")
                .body(request)
                .post("/api/createProject");
    }

    // ✅ CREATE PROJECT - INVALID CONTENT TYPE (for testing 415 error)
    public static Response createProjectInvalidContentType(Object request) {

        System.out.println("Executing with INVALID content-type");

        return given()
                .contentType(ContentType.TEXT)  // text/plain
                .header("Authorization", TokenUtil.getToken(UserRole.SUPER_ADMIN))
                .body(request)
                .post("/api/createProject");
    }

    // ✅ GET MY PROJECTS - WITH ROLE SUPPORT
    public static Response getMyProjects(UserRole role) {

        String payload = "{ \"userId\": \"" + TokenUtil.getUserId(role) + "\" }";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken(role))
                .body(payload)
                .when()
                .post("/api/getMyProjects");
    }

    // ✅ GET MY PROJECTS - DEFAULT (SUPER_ADMIN)
    public static Response getMyProjects() {
        return getMyProjects(UserRole.SUPER_ADMIN);
    }

    // ✅ GET MY PROJECTS - WITH REQUEST OBJECT AND ROLE (STRING)
    public static Response getMyProjects(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        // Authorization handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/getMyProjects");
    }

    // ✅ GET PROJECT DETAILS BY PROJECT ID - WITH ROLE SUPPORT
    public static Response getProjectDetailsByProjectId(int projectId, UserRole role) {

        String payload = "{"
                + "\"userId\":\"" + TokenUtil.getUserId(role) + "\","
                + "\"projectId\":\"" + projectId + "\""
                + "}";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken(role))
                .body(payload)
                .when()
                .post("/api/getProjectDetailsByProjectId");
    }

    // ✅ GET PROJECT DETAILS - DEFAULT (SUPER_ADMIN)
    public static Response getProjectById(Object request, String role) {

        var req = given()
                .contentType(ContentType.JSON);

        // Auth handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/getProjectDetailsByProjectId");
    }


    // ✅ GET PROJECTS - WITH ROLE SUPPORT
    public static Response getProjects(UserRole role) {

        String payload = "{"
                + "\"userId\":\"" + TokenUtil.getUserId(role) + "\","
                + "\"orgId\":\"1\""
                + "}";

        return given()
                .contentType("application/json")
                .header("Authorization", TokenUtil.getToken(role))
                .body(payload)
                .when()
                .post("/api/getProjects");
    }

    // ✅ GET PROJECTS - DEFAULT (SUPER_ADMIN)
    public static Response getProjects() {
        return getProjects(UserRole.SUPER_ADMIN);
    }

    // ✅ EDIT PROJECT - WITH ROLE SUPPORT
    public static Response editProject(Object request, String role) {
        // 1. Get Base URI from a central config or system property to avoid hardcoding
        String baseUri = System.getProperty("base.uri", "https://test.cognitest.ai");

        var req = given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .log().ifValidationFails(); // Good practice for debugging failed dynamic tests

        // 2. Dynamic Authorization
        if (role != null && !"NO_AUTH".equalsIgnoreCase(role)) {
            String token = TokenUtil.getToken(role);
            if (token != null) {
                req.header("Authorization", "Bearer " + token);
            }
        }

        // 3. Attach dynamic request body
        if (request != null) {
            req.body(request);
        }

        // 4. Execute PUT request
        Response response = req.when().put("/api/editProject");

        // 5. Optional: Log response details for troubleshooting dynamic data
        System.out.println("🚀 API Request sent to: " + baseUri + "/api/editProject");

        return response;
    }
    // ✅ GET USER MANAGEMENT DETAILS FOR PROJECT ID
    public static Response getUserManagementDetailsForProjectId(Object request, String role) {

        var req = given()
                .baseUri("https://test.cognitest.ai")
                .contentType(ContentType.JSON);

        // Auth handling
        if (!"NO_AUTH".equalsIgnoreCase(role)) {
            req.header("Authorization", TokenUtil.getToken(role));
        }

        if (request != null) {
            req.body(request);
        }

        return req.post("/api/getUserManagementDetailsForProjectId");
    }
}