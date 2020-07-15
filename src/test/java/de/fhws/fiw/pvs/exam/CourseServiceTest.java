package de.fhws.fiw.pvs.exam;

import com.owlike.genson.Genson;
import de.fhws.fiw.pvs.exam.database.dao.CourseDAO;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.database.daoimpl.CourseDAOImpl;
import de.fhws.fiw.pvs.exam.database.daoimpl.EventDAOImpl;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import de.fhws.fiw.pvs.exam.resources.Course;
import de.fhws.fiw.pvs.exam.resources.Event;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/***
 * By Luca Lanzo
 */


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CourseServiceTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills/courses";
    private static final CourseDAO courseDatabase = new CourseDAOImpl("courses", Course.class);
    private static final EventDAO eventDatabase = new EventDAOImpl("events", Event.class);
    private Course testCourse;
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
        studentCreds = "Basic " + Base64.encodeBase64String("admin:admin".getBytes());
    }


    // POST a course
    @Test
    @Order(1)
    public void createCourseTest() {
        try {
            testCourse = new Course("Testcourse", "A test course for JUnit", 50);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

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
                        .contains("http://localhost:8080/api/softskills/courses/" + testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET all courses as admin
    @Test
    @Order(2)
    public void getAllCoursesAsAdminTest() {
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
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET all courses as student
    @Test
    @Order(3)
    public void getAllCoursesAsStudentTest() {
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
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET course by id
    @Test
    @Order(4)
    public void getCourseByIdTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET course by name
    @Test
    @Order(5)
    public void getCourseByNameTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/?courseName=" + testCourse.getCourseName())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // GET all the events from a specific course
    @Test
    @Order(6)
    public void getAllEventsFromSpecificCourseTest() {
        try {
            testEvent = new Event("2020-07-18--18:00:00", "2020-07-18--18:00:00");
            testEvent.setCourseId(BASE_URL + "/" + testCourse.getHashId());

            eventDatabase.insertInto(testEvent);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId() + "/events")
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

    // GET a specific event from a specific course
    @Test
    @Order(7)
    public void getSpecificEventFromSpecificCourseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId() + "/events/" + testEvent.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertTrue(Objects.requireNonNull(response.body()).string().contains(testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    // PUT a course
    @Test
    @Order(8)
    public void updateCourseTest() {
        try {
            testCourse.setCourseName("TestcoursePutTest");
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .header("Authorization", adminCreds)
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertTrue(body.contains(testCourse.getHashId()) && body.contains("TestcoursePutTest"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }


    // DELETE a course
    @Test
    @Order(9)
    public void deleteCourseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .delete()
                    .header("Authorization", adminCreds)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .header("Authorization", adminCreds)
                    .build();

            response = client.newCall(request).execute();

            String body = Objects.requireNonNull(response.body()).string();
            assertFalse(body.contains(testCourse.getHashId()) && body.contains("TestcoursePutTest"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
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