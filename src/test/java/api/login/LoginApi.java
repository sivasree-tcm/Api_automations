package api.login;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class LoginApi {

    public static Response login(String email, String password) {
        System.out.println("\n📧 Login Request:");
        System.out.println("Email: " + email);

        String payload = "{"
                + "\"userEmail\":\"" + email + "\","
                + "\"userPassword\":\"" + password + "\""
                + "}";

        Response response = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(payload)
                  // Log request details
                .when()
                .post("/api/login")
                .then()
                .extract()
                .response();

        // Debug output


        return response;
    }
}