package api;

import okhttp3.*;

import java.io.IOException;

/***
 * By Luca Lanzo
 */


public class AuthTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "https://api.fiw.fhws.de/auth/api/users/me";

    public static void main(String[] args) throws IOException {
        OkHttpClient client = createAuthenticatedClient("k46471", "Mnihup@21");

        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(request.header("Authorization"));
        System.out.println();
        System.out.println(response.header("X-fhws-jwt-token"));
    }

    private static OkHttpClient createAuthenticatedClient(String username, String password) {
       return new OkHttpClient.Builder().authenticator((route, response) -> {
           String credential = Credentials.basic(username, password);
           return response.request().newBuilder().header("Authorization", credential).build();
       }).build();
    }
}
