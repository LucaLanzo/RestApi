package authorization;


import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/***
 * By Luca Lanzo
 */


public class Authorization {
    private final static String BASE_URL = "https://api.fiw.fhws.de/auth/api/users/me";

    public static String[] authorizeUser(String authBody) throws IOException {
        // TODO: These are debug admin creds, delete these if you want a good grade, cause you should never hardcode creds
        if (authBody.equals("Basic YWRtaW46YWRtaW4=")) {
            return new String[]{("ADMIN CREDS PLEASE DELETE"), ("False")};
        }

        // Returns 401 and false if wrong creds --- returns jwt and true if right creds and student
        if (authBody.equals("")) {
            return new String[]{("401"), ("False")};
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .header("Authorization", authBody)
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 401) {
            return new String[]{"401", "False"};
        } else {
            String isStudent = "False";
            if (Objects.requireNonNull(response.body()).string().contains("student")) isStudent = "True";
            return new String[]{response.header("X-fhws-jwt-token"), isStudent};
        }
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
