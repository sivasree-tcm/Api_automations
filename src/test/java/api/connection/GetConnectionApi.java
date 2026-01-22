package api.connection;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.TokenUtil;
import tests.roles.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetConnectionApi {

    // 🔥 Holds extracted connection IDs
    private static final List<Integer> connectionIds = new ArrayList<>();

    public static Response getConnections(
            Object request,
            String role,
            String authType
    ) {

        var req = given()
                .contentType(ContentType.JSON);

        // 🔐 Authorization
        if ("MISSING".equalsIgnoreCase(authType)) {
            // no auth
        } else if ("INVALID".equalsIgnoreCase(authType)) {
            req.header("Authorization", "Bearer invalid_token");
        } else {
            req.header(
                    "Authorization",
                    TokenUtil.getToken(UserRole.valueOf(role))
            );
        }

        if (request != null) {
            req.body(request);
        }

        Response response = req
                .when()
                .post("/api/getConnections");

        // 🔥 Extract IDs here
        extractConnectionIds(response);

        return response;
    }

    // ================= EXTRACTION =================

    private static void extractConnectionIds(Response response) {

        connectionIds.clear();

        List<Map<String, Object>> data =
                response.jsonPath().getList("data");

        if (data == null || data.isEmpty()) {
            return;
        }

        for (Map<String, Object> obj : data) {
            Integer id = (Integer) obj.get("id");
            if (id != null) {
                connectionIds.add(id);
            }
        }

        System.out.println("🔍 Extracted Connection IDs: " + connectionIds);
    }


    public static List<Integer> getExtractedConnectionIds() {
        return connectionIds;
    }
}
