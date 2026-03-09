package api.organization;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import utils.TokenUtil;

public class GetOrganizationsApi {

    public static Response getOrganizations(
            String role,
            String authType
    ) {

        var req = given().contentType("application/json");

        if ("MISSING".equalsIgnoreCase(authType)) {

        }
        else if ("INVALID".equalsIgnoreCase(authType)) {

            req.header("Authorization", "Bearer invalid_token");
        }
        else {

            req.header(
                    "Authorization",
                    TokenUtil.getToken(
                            tests.roles.UserRole.valueOf(role)
                    )
            );
        }

        return req.get("/api/Organizations");
    }
}