package tests.login;

import base.BaseTest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class LoginTest extends BaseTest {

    public static String authToken;
    public static String csrfToken;

    @Test
    public void loginTest() {

        String payload = "{\n" +
                "  \"userEmail\": \"sivasree@tickingminds.com\",\n" +
                "  \"userPassword\": \"Sivasree@172\"\n" +
                "}";

        Response response =
                given()
                        .contentType("application/json")
                        .body(payload)
                        .when()
                        .post("/api/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        authToken = response.getHeader("Authorization");
        csrfToken = response.getHeader("X-CSRF-Token");

        System.out.println("Authorization Token = " + authToken);
        System.out.println("CSRF Token = " + csrfToken);
    }
}
