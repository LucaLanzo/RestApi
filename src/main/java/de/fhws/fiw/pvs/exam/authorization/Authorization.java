package de.fhws.fiw.pvs.exam.authorization;


import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import okhttp3.*;

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
        // Returns 401 and false if no creds have been transmitted
        if (authBody.equals("")) {
            return new String[]{("401"), ("other"), ("")};
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .header("Authorization", authBody)
                .build();

        // Make a request to fiw.fhws api
        Response response = client.newCall(request).execute();
        String body = Objects.requireNonNull(response.body()).string();

        // If fiw.fhws api denies the connection return the 401 to the client
        if (response.code() == 401) {
            return new String[]{("401"), ("other"), ("")};
        // If fiw.fhws api accepts the connection return the jwt token, the role and the cn
        } else {
            return new String[]{response.header("X-fhws-jwt-token"), getValue(body, "role"),
                    getValue(body, "cn")};
        }
    }


    private static String getValue(String body, String key) {
        Genson builder = new Genson();
        Map<String, String> student = builder.deserialize(body, new GenericType<HashMap<String, String>>() {});
        return student.get(key);
    }


    public static javax.ws.rs.core.Response getWWWAuthenticateResponse(String realm) {
        // A built response when wrong creds get entered.
        // Full class path needed because OkHttpClient has a Request/Response class too
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", "realm=" + realm)
                .build();
    }

    public static javax.ws.rs.core.Response getWrongRoleResponse() {
        // The built response when wrong role (e.g.: You are not a student) is used.
        // Full class path needed because OkHttpClient has a Request/Response class too
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.FORBIDDEN)
                .build();
    }
}
