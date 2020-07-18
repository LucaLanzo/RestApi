package de.fhws.fiw.pvs.exam.service;

import com.owlike.genson.Genson;
import de.fhws.fiw.pvs.exam.database.DAOFactory;
import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import de.fhws.fiw.pvs.exam.resources.Course;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CourseServiceExceptionTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final CourseDAO courseDatabase = DAOFactory.createCourseDAO();
    private static String BASE_URL = "";
    private Course testCourse;
    private Genson builder;
    private OkHttpClient client;
    private String adminCreds;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
        adminCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());

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
            if (link.contains("course")) eventLink = link;
        }
        BASE_URL = eventLink.substring(eventLink.indexOf("<") + 1, eventLink.indexOf(">"));
    }


    // POST with missing courseName. RETURN CODE 400
    @Test
    @Order(1)
    public void createCourseWithoutNameTest() {
        try {
            testCourse = new Course("", "A de.fhws.fiw.pvs.exam.test course for JUnit", 50);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            courseDatabase.delete(testCourse.getHashId());
            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with missing courseDescription. RETURN CODE 400
    @Test
    @Order(2)
    public void createCourseWithoutDescriptionTest() {
        try {
            testCourse = new Course("Testcourse", "", 50);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            courseDatabase.delete(testCourse.getHashId());
            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // POST with unachievable maxStudents amount. RETURN CODE 400
    @Test
    @Order(3)
    public void createCourseWithImpossibleMaxStudentsTest() {
        try {
            testCourse = new Course("Testcourse", "A de.fhws.fiw.pvs.exam.test course for JUnit", -1);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            courseDatabase.delete(testCourse.getHashId());
            assertEquals(400, response.code());
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET a course that is not in the de.fhws.fiw.pvs.exam.de.fhws.fiw.pvs.exam.database. RETURN CODE 404
    @Test
    @Order(4)
    public void getCourseNotInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInTheDatabase")
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


    // GET a specific event from specific course but the specific event can't be found. RESPONSE CODE 404
    @Test
    @Order(5)
    public void getSpecificEventFromSpecificCourseButNoEventInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId() + "/events/EventThatDoesntExist")
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


    // PUT a default course (= all attributes left empty). RESPONSE CODE 400
    @Test
    @Order(6)
    public void updateCourseWithDefaultsTest() {
        try {
            testCourse.setCourseName("");
            testCourse.setCourseDescription("");
            testCourse.setMaximumStudents(0);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .put(requestBody)
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


    // PUT with an invalid maxStudents. RESPONSE CODE 400
    @Test
    @Order(7)
    public void updateCourseWithInvalidMaxStudentsTest() {
        try {
            testCourse.setMaximumStudents(-1);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .put(requestBody)
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


    // PUT with a course that is not in the de.fhws.fiw.pvs.exam.de.fhws.fiw.pvs.exam.database. RESPONSE CODE 404
    @Test
    @Order(8)
    public void updateCourseThatIsNotInDatabaseTest() {
        try {
            testCourse = new Course("TestcoursePutTest", "A de.fhws.fiw.pvs.exam.test course for JUnit",
                    50);
            RequestBody requestBody = RequestBody.create(JSON, builder.serialize(testCourse));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInDatabase")
                    .put(requestBody)
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            courseDatabase.delete(testCourse.getHashId());
            assertEquals(404, response.code());
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE a course that is not in de.fhws.fiw.pvs.exam.de.fhws.fiw.pvs.exam.database. RESPONSE CODE 404
    @Test
    @Order(9)
    public void deleteCourseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInDatabase")
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
    }
}