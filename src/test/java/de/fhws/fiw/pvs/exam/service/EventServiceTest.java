package de.fhws.fiw.pvs.exam.service;

import com.owlike.genson.Genson;
import de.fhws.fiw.pvs.exam.database.DAOFactory;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import javassist.bytecode.ByteArray;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import de.fhws.fiw.pvs.exam.resources.Event;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
    private static final EventDAO eventDatabase = DAOFactory.createEventDAO();
    private static String BASE_URL = "";
    private Event testEvent;
    private Genson builder;
    private OkHttpClient client;
    private String adminCreds;
    private String studentCreds;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
        adminCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
        studentCreds = "Basic " + Base64.encodeBase64String("student:student".getBytes());

        Response response = null;
        try {
            Request request = new Request.Builder()
                    .url("http://localhost:8080/api/softskills")
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            response = client.newCall(request).execute();
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }

        List<String> allLinkHeaders = response.headers("Link");
        String eventLink = "";
        for (String link : allLinkHeaders) {
            if (link.contains("event")) eventLink = link;
        }
        BASE_URL = eventLink.substring(eventLink.indexOf("<") + 1, eventLink.indexOf(">"));
    }


    // POST an event
    @Test
    @Order(1)
    public void createEventTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            testEvent.setSignedUpStudents(new HashSet<>());
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
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


    // GET all events as admin
    @Test
    @Order(2)
    public void getAllEventsTestAsAdmin() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testEvent.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET all events as students
    @Test
    @Order(3)
    public void getAllEventsTestAsStudent() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testEvent.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET event by id
    @Test
    @Order(4)
    public void getEventByIdTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testEvent.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // CourseService: GET all events cacheControl check
    @Test
    @Order(5)
    public void getAllEventsCacheControlTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertNotNull(response.header("Cache-Control"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // CourseService: GET single cacheControl check
    @Test
    @Order(6)
    public void getAllSingleEventCacheControlTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertNotNull(response.header("Cache-Control"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT an event
    @Test
    @Order(7)
    public void updateEventTest() {
        try {
            testEvent.setStartTime("2020-07-18--19:00:00");
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .header("Authorization", adminCreds)
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertTrue(body.contains(testEvent.getHashId()) && body.contains("2020-07-18--19:00:00"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT as a student to sign up for event
    @Test
    @Order(8)
    public void putAsStudentToSignUpTest() {
        try {
            testEvent.setStartTime("2020-07-18--19:00:00");
            testEvent.setEndTime("2020-07-18--20:00:00");
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .header("Authorization", studentCreds)
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(response.code());
            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertTrue(body.contains(testEvent.getHashId()) && body.contains("k11111"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE as student to leave an event
    @Test
    @Order(9)
    public void deleteAsStudentToLeaveTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .delete()
                    .header("Authorization", studentCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", studentCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertFalse(body.contains(testEvent.getHashId()) && body.contains("k11111"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE an event
    @Test
    @Order(10)
    public void deleteEventTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .delete()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertFalse(body.contains(testEvent.getHashId()) && body.contains("2020-07-18--18:00:00"));
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
