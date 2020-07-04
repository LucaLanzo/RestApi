package authorization;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * By Luca Lanzo
 */

public class Authorization {
    private final static String BASE_URL = "https://api.fiw.fhws.de/auth/api/users/me";

    public static String[] authorizeUser(String authBody) throws IOException {
        // Returns 401 and False if wrong creds, and jwt and true if right creds and student
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
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED)
                .header("WWW-Authenticate", realm)
                .build();
    }
}
