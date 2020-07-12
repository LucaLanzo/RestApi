package service;

import com.owlike.genson.Genson;
import database.dao.EventDAO;
import database.daoimpl.EventDAOImpl;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import resources.Event;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/***
 * By Luca Lanzo
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventServiceTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills/events";
    private static final EventDAO eventDatabase = new EventDAOImpl("events", Event.class);
    private Event testEvent;
    private Genson builder;
    private OkHttpClient client;
    private String authorizationCreds;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
        authorizationCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
    }


    // POST an event
    @Test
    @Order(1)
    public void createEventTest() {
        try {
            testEvent = new Event(1000, 1200, 10120);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 201) {
                fail("Wrong response code.");
            } else {
                assertTrue(Objects.requireNonNull(response.header("Location"))
                        .contains("http://localhost:8080/api/softskills/events/" + testEvent.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET all events
    @Test
    @Order(2)
    public void getAllEventsTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains("1000"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET event by id
    @Test
    @Order(3)
    public void getEventByIdTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains("1000"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT an event
    @Test
    @Order(4)
    public void updateEventTest() {
        try {
            testEvent.setStartTime(800);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .header("Authorization", authorizationCreds)
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            response = client.newCall(request).execute();

            assertTrue(Objects.requireNonNull(response.body()).string().contains("800"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE an event
    @Test
    @Order(5)
    public void deleteEventTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .delete()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            response = client.newCall(request).execute();


            assertFalse(Objects.requireNonNull(response.body()).string().contains("800"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @AfterAll
    public void tearDown() {
        Event event = eventDatabase.getById(testEvent.getHashId());
        if (event != null) {
            eventDatabase.delete(testEvent.getHashId());
        }
    }
}
