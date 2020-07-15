package de.fhws.fiw.pvs.exam;

import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class StartServiceTest {
    private final static String BASE_URL = "http://localhost:8080/api/softskills";
    private OkHttpClient client;
    private String adminCreds;
    private String studentCreds;

    @BeforeAll
    public void setUp() {
        client = new OkHttpClient();
        adminCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
        studentCreds = "Basic " + Base64.encodeBase64String("student:student".getBytes());
    }


    // Test to do anything with no credentials. As every CRUD implements the same de.fhws.fiw.pvs.exam.authorization I only have to
    // test "wrong creds" and "no creds" once. RETURN CODE 401
    @Test
    public void accessServerWithNoCreds() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(401, response.code());
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
                    .header("Authorization", Base64.encodeBase64String("absolutely:wrong".getBytes()))
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(401, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    // Test the server with admin creds
    @Test
    public void accessServerWithAdminCreds() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(204, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    // Test the server with student creds
    @Test
    public void accessServerWithStudentCreds() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(204, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    // Test dispatcher headers
    @Test
    public void testDispatcherHeaders() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            String allHeaders = Objects.requireNonNull(response.headers()).toString();

            boolean containsCourseDispatcher = allHeaders.contains(BASE_URL + "/courses>;");
            boolean containsEventDispatcher = allHeaders.contains("<http://localhost:8080/api/softskills/events>;");

            assertTrue(containsCourseDispatcher && containsEventDispatcher);
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }
}