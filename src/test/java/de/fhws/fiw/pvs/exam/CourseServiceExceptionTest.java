package de.fhws.fiw.pvs.exam;

import com.owlike.genson.Genson;
import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import de.fhws.fiw.pvs.exam.database.daoimpl.CourseDAOImpl;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import de.fhws.fiw.pvs.exam.resources.Course;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CourseServiceExceptionTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills/courses";
    private static final CourseDAO courseDatabase = new CourseDAOImpl("courses", Course.class);
    private Course testCourse;
    private Genson builder;
    private OkHttpClient client;
    private String authorizationCreds;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
        authorizationCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
    }


    // POST with missing courseName. RETURN CODE 400
    @Test
    @Order(1)
    public void createCourseWithoutNameTest() {
        try {
            testCourse = new Course("", "A test course for JUnit", 50);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                courseDatabase.delete(testCourse.getHashId());
                fail("Response code should have been 400");
            } else {
                courseDatabase.delete(testCourse.getHashId());
                assertEquals(400, response.code());
            }
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
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                courseDatabase.delete(testCourse.getHashId());
                fail("Response code should have been 400");
            } else {
                courseDatabase.delete(testCourse.getHashId());
                assertEquals(400, response.code());
            }
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
            testCourse = new Course("Testcourse", "A test course for JUnit", -1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 400) {
                courseDatabase.delete(testCourse.getHashId());
                fail("Response code should have been 400");
            } else {
                courseDatabase.delete(testCourse.getHashId());
                assertEquals(400, response.code());
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET a course that is not in the de.fhws.fiw.pvs.exam.database. RETURN CODE 404
    @Test
    @Order(4)
    public void getCourseNotInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInTheDatabase")
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


    // GET a specific event from specific course but the specific event can't be found. RESPONSE CODE 404
    @Test
    @Order(5)
    public void getSpecificEventFromSpecificCourseButNoEventInDatabaseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId() + "/events/EventThatDoesntExist")
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


    // PUT a default course (= all attributes left empty). RESPONSE CODE 400
    @Test
    @Order(6)
    public void updateCourseWithDefaultsTest() {
        try {
            testCourse.setCourseName("");
            testCourse.setCourseDescription("");
            testCourse.setMaximumStudents(0);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
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


    // PUT with an invalid maxStudents. RESPONSE CODE 400
    @Test
    @Order(7)
    public void updateCourseWithInvalidMaxStudentsTest() {
        try {
            testCourse.setMaximumStudents(-1);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
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


    // PUT with a course that is not in the de.fhws.fiw.pvs.exam.database. RESPONSE CODE 404
    @Test
    @Order(8)
    public void updateCourseThatIsNotInDatabaseTest() {
        try {
            testCourse = new Course("TestcoursePutTest", "A test course for JUnit",
                    50);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInDatabase")
                    .put(requestBody)
                    .header("Authorization", authorizationCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 404) {
                courseDatabase.delete(testCourse.getHashId());
                fail("Response code should have been 404");
            } else {
                courseDatabase.delete(testCourse.getHashId());
                assertEquals(404, response.code());
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE a course that is not in de.fhws.fiw.pvs.exam.database. RESPONSE CODE 404
    @Test
    @Order(9)
    public void deleteCourseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + "CourseThatIsNotInDatabase")
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
        Course course = courseDatabase.getById(testCourse.getHashId());
        if (course != null) {
            courseDatabase.delete(testCourse.getHashId());
        }
    }
}