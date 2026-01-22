package utils;

import api.connection.DeleteConnectionApi;
import api.connection.GetConnectionApi;
import io.restassured.response.Response;
import io.restassured.builder.ResponseBuilder;
import tests.connection.ConnectionReport;

import java.util.List;
import java.util.Map;

public class DeleteConnectionUtil {

    public static Response deleteAllConnections(ConnectionReport.TestCase tc) {

        // Step 1: Fetch connections
        GetConnectionApi.getConnections(
                tc.getRequest(),
                tc.getRole(),
                "VALID"
        );

        List<Integer> connectionIds =
                GetConnectionApi.getExtractedConnectionIds();

        System.out.println("🔹 Extracted Connection IDs: " + connectionIds);

        // ✅ Case 1: No connections → PASS
        if (connectionIds == null || connectionIds.isEmpty()) {
            return successResponse("No connections found to delete");
        }

        // ✅ Case 2: Delete all connections
        for (Integer id : connectionIds) {
            DeleteConnectionApi.deleteConnection(
                    buildDeleteRequest(tc, id),
                    tc.getRole(),
                    tc.getAuthType()
            );
            System.out.println("🗑 Deleted connectionId = " + id);
        }

        return successResponse("All connections deleted successfully");
    }

    // ---------------- PRIVATE HELPERS ----------------

    private static Object buildDeleteRequest(
            ConnectionReport.TestCase tc,
            Integer connId
    ) {

        final Integer userIdVal =
                Integer.valueOf(((Map<?, ?>) tc.getRequest()).get("userId").toString());

        final Integer orgIdVal =
                Integer.valueOf(((Map<?, ?>) tc.getRequest()).get("orgId").toString());

        final Integer connectionIdVal = connId;

        return new Object() {
            public final Integer userId = userIdVal;
            public final Integer orgId = orgIdVal;
            public final Integer connectionId = connectionIdVal;
        };
    }



    private static Response successResponse(String message) {
        return new ResponseBuilder()
                .setStatusCode(200)
                .setBody("{\"message\":\"" + message + "\"}")
                .build();
    }
}
