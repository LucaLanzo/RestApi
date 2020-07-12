package service;

import com.owlike.genson.Genson;
import database.dao.EventDAO;
import database.daoimpl.EventDAOImpl;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import resources.Event;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class EventServiceExceptionTest {
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


    // POST with wrong startTime. RETURN CODE 400
    @Test
    @Order(1)
    public void createEventWithWrongStartTimeTest() {
        try {
            testEvent = new Event(-1, 1200, 10120);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with wrong endTime. RETURN CODE 400
    @Test
    @Order(2)
    public void createEventWithWrongEndTimeTest() {
        try {
            testEvent = new Event(1000, -1, 10120);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with wrong date. RETURN CODE 400
    @Test
    @Order(3)
    public void createEventWithWrongDateTest() {
        try {
            testEvent = new Event(1000, 1200, -1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET an event that is not in the database. RETURN CODE 404
    @Test
    @Order(4)
    public void getEventNotInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInTheDatabase")
                    .get()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 404) {
                fail("Response code should have been 404");
            } else {
                assertEquals(404, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT a default event (= all attributes left empty). RESPONSE CODE 400
    @Test
    @Order(5)
    public void updateEventWithDefaultsTest() {
        try {
            testEvent.setStartTime(0);
            testEvent.setEndTime(0);
            testEvent.setDate(0);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an invalid startTime. RESPONSE CODE 400
    @Test
    @Order(6)
    public void updateEventWithWrongStartTimeTest() {
        try {
            testEvent.setStartTime(-1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an invalid endTime. RESPONSE CODE 400
    @Test
    @Order(7)
    public void updateEventWithWrongEndTimeTest() {
        try {
            testEvent.setEndTime(-1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an invalid date. RESPONSE CODE 400
    @Test
    @Order(8)
    public void updateEventWithWrongDateTest() {
        try {
            testEvent.setDate(-1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() != 400) {
                fail("Response code should have been 400");
            } else {
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an event that is not in the database. RESPONSE CODE 404
    @Test
    @Order(9)
    public void updateEventThatIsNotInDatabaseTest() {
        try {
            testEvent = new Event(1000, 1200, 101020);
            RequestBody requestBody = RequestBody.create(builder.serialize(testEvent), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInDatabase")
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 404) {
                fail("Response code should have been 404");
            } else {
                assertEquals(404, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE an event that is not in database. RESPONSE CODE 404
    @Test
    @Order(10)
    public void deleteEventTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInDatabase")
                    .delete()
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 404) {
                fail("Response code should have been 404");
            } else {
                assertEquals(404, response.code());
            }
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