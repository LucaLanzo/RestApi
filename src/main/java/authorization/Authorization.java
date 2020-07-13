package authorization;


import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import okhttp3.*;

import javax.ws.rs.core.GenericEntity;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/***
 * By Luca Lanzo
 */


public class Authorization {
    private final static String BASE_URL = "https://api.fiw.fhws.de/auth/api/users/me";

    public static String[] authorizeUser(String authBody) throws IOException {
        // TODO: THESE ARE HARDCODED DEBUG ADMIN CREDS (DELETE WHEN IN PRODUCTION)
        if (authBody.equals("Basic YWRtaW46YWRtaW4=")) {
            return new String[]{("ADMIN CREDS PLEASE DELETE"), ("other"), ("")};
        }
        // TODO: THESE ARE HARDCODED DEBUG ADMIN CREDS (DELETE WHEN IN PRODUCTION)
        if (authBody.equals("Basic c3R1ZGVudDpzdHVkZW50")) {
            return new String[]{("ADMIN CREDS PLEASE DELETE"), ("student"), ("k11111")};
        }

        // Returns 401 and false if wrong creds --- returns jwt and true if right creds and student
        if (authBody.equals("")) {
            return new String[]{("401"), ("other"), ("")};
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .header("Authorization", authBody)
                .build();

        Response response = client.newCall(request).execute();
        String body = Objects.requireNonNull(response.body()).string();

        if (response.code() == 401) {
            return new String[]{("401"), ("other"), ("")};
        } else {
            String role = "other";
            if (body.contains("student")) role = "student";
            return new String[]{response.header("X-fhws-jwt-token"), role, getcn(body)};
        }
    }


    private static String getcn(String body) {
        Genson builder = new Genson();
        Map<String, String> student = builder.deserialize(body, new GenericType<HashMap<String, String>>() {});
        return student.get("cn");
    }


    public static javax.ws.rs.core.Response getWWWAuthenticateResponse(String realm) {
        // The built response for wrong creds. Gotta use the full class path because OkHttpClient has a Request/Response
        // class too
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", realm)
                .build();
    }

    public static javax.ws.rs.core.Response getWrongRoleResponse() {
        // The built response for wrong role (e.g.: You are not a student). Same as above with the class path
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN)
                .build();
    }
}
