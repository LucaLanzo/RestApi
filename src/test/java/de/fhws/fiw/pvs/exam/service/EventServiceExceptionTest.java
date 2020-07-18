package de.fhws.fiw.pvs.exam.service;

import com.owlike.genson.Genson;
import de.fhws.fiw.pvs.exam.database.DAOFactory;
import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.resources.Course;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import de.fhws.fiw.pvs.exam.resources.Event;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class EventServiceExceptionTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final CourseDAO courseDatabase = DAOFactory.createCourseDAO();
    private static final EventDAO eventDatabase = DAOFactory.createEventDAO();
    private static String BASE_URL = "";
    private Event testEvent;
    private Course testCourse;
    private Genson builder;
    private OkHttpClient client;
    private String adminCreds;
    private String studentCreds;
    private String student2Creds;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
        adminCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
        studentCreds = "Basic " + Base64.encodeBase64String("student:student".getBytes());
        student2Creds = "Basic " + Base64.encodeBase64String("student2:student2".getBytes());

        // Get the BASE_URL from the dispatcher
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


    // POST with missing startTime/endTime. RETURN CODE 400
    @Test
    @Order(1)
    public void createEventWithNoTimeTest() {
        try {
            testEvent = new Event();
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with wrong time format. RETURN CODE 400
    @Test
    @Order(2)
    public void createEventWithWrongTimeFormatTest() {
        try {
            testEvent = new Event("bla", "bla");
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with wrong startTime after endTime. RETURN CODE 400
    @Test
    @Order(3)
    public void createEventWithStartAfterEndTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--16:00:00");
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with signedUpStudents > course.maximumStudents. RESPONSE CODE 400
    @Test
    @Order(4)
    public void putWithFullCourseTest() {
        try {
            testCourse = new Course("testCourse", "testDescription", 1);
            courseDatabase.insertInto(testCourse);

            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            testEvent.setCourseId(testCourse.getHashId());
            HashSet<String> hs = new HashSet<>();
            hs.add("1"); hs.add("2");
            testEvent.setSignedUpStudents(hs);

            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET an event that is not in the database. RETURN CODE 404
    @Test
    @Order(5)
    public void getEventNotInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInTheDatabase")
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(404, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an event that is not in the database. RESPONSE CODE 404
    @Test
    @Order(6)
    public void updateEventThatIsNotInDatabaseTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInDatabase")
                    .put(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(404, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with an course that is not in the database. RESPONSE CODE 400
    @Test
    @Order(9)
    public void updateEventWithCourseNotInDatabaseTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            testEvent.setCourseId(testCourse.getHashId());
            eventDatabase.insertInto(testEvent);

            testEvent.setStartTime("2020-07-18--19:00:00");
            testEvent.setEndTime("2020-07-18--20:00:00");
            testEvent.setCourseId("DefinitelyNotInTheDatabase");

            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            eventDatabase.delete(testEvent.getHashId());

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT with signedUpStudents list bigger than course maxStudents. RESPONSE CODE 400
    @Test
    @Order(10)
    public void updateEventListBiggerThanMaximumStudentsTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            testEvent.setCourseId(testCourse.getHashId());
            testEvent.setSignedUpStudents(new HashSet<>());
            eventDatabase.insertInto(testEvent);

            HashSet<String> hs = new HashSet<>();
            hs.add("k11111");
            hs.add("k22222");
            testEvent.setSignedUpStudents(hs);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            eventDatabase.delete(testEvent.getHashId());

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // PUT as student with full course. RESPONSE CODE 400
    @Test
    @Order(11)
    public void putAsStudentWithFullCourseTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--20:00:00");
            testEvent.setSignedUpStudents(new HashSet<>());
            testEvent.setCourseId(testCourse.getHashId());
            eventDatabase.insertInto(testEvent);

            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testEvent));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", studentCreds)
                    .build();

            client.newCall(request).execute();

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testEvent.getHashId())
                    .put(requestBody)
                    .header("Authorization", student2Creds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE an event that is not in database. RESPONSE CODE 404
    @Test
    @Order(11)
    public void deleteEventTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "EventThatIsNotInDatabase")
                    .delete()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            assertEquals(404, response.code());
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    @AfterAll
    public void tearDown() {
        Course course = courseDatabase.getById(testCourse.getHashId());
        if (course != null) {
            courseDatabase.delete(testCourse.getHashId());
        }
        Event event = eventDatabase.getById(testEvent.getHashId());
        if (event != null) {
            eventDatabase.delete(testEvent.getHashId());
        }
    }
}