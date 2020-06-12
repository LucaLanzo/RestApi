package service;

import com.owlike.genson.Genson;
import database.MongoOperations;
import okhttp3.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ressources.Course;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CourseServiceTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills/courses";
    private static MongoOperations<Course> courseDatabase = new MongoOperations<>("courses", Course.class);
    private Course testCourse;
    private Genson builder;
    private OkHttpClient client;

    @BeforeAll
    public void setUp() {
        builder = new Genson();
        client = new OkHttpClient();
    }

    @Test
    @Order(1)
    public void createCourse() {
        try {
            testCourse = new Course("Testcourse");
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 201) {
                fail("Wrong or no response code.");
            } else {
                assertTrue(response.header("Location").contains("http://localhost:8080/api/softskills/courses/ "
                                + testCourse.getHashId()));
            }
        } catch (NullPointerException e) {
            fail("No location header has been sent by the server.");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(2)
    public void getAllCourses() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            assertTrue(response.body().string().contains("Testcourse"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(3)
    public void getCourseById() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            assertTrue(response.body().string().contains("Testcourse"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(4)
    public void getCourseByName() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/?name=" + testCourse.getName())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            assertTrue(response.body().string().contains("Testcourse"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(5)
    public void updateCourse() {
        try {

            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            assertTrue(response.body().string().contains("TestcoursePut"));
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    public void deleteCourse() {
    }

    @AfterAll
    public void tearDown() throws IOException {
        Course course = courseDatabase.getByName("TestKurs");
        if (!(course == null)) {
            courseDatabase.delete(course.getHashId());
        }
    }
}