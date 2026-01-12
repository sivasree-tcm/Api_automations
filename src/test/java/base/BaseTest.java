package base;

import io.restassured.RestAssured;

public class BaseTest {

    static {
        RestAssured.baseURI = "https://test.cognitest.ai";
    }
}
