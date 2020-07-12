package service;

import com.owlike.genson.Genson;
import database.daoimpl.CourseDAOImpl;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import resources.Course;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class StartServiceTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills";
    private OkHttpClient client;
    private String authorizationCreds;

    @BeforeAll
    public void setUp() {
        client = new OkHttpClient();
        authorizationCreds = "Basic " + Base64.encodeBase64String("wrongUser:wrongPassword".getBytes());
    }


    // Test to do anything with no credentials. As every CRUD implements the same authorization I only have to
    // test "wrong creds" and "no creds" once. RETURN CODE 401
    @Test
    public void accessServerWithNoCreds() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 401) {
                fail("Response code should have been 401");
            } else {
                assertEquals(401, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // Test to do anything with wrong credentials. RETURN CODE 401
    @Test
    public void accessServerWithWrongCreds() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 401) {
                fail("Response code should have been 401");
            } else {
                assertEquals(401, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // I would normally test to "POST/PUT/DELETE with wrong role" here but that would mean that I would have to hardcode
    // my fhws student login credentials so I am not going to do that. And testing against the login api doesn't really
    // make any sense here as I am trying to test my own server.
}