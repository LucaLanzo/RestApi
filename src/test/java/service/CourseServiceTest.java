package service;

import com.owlike.genson.Genson;
import database.daoimpl.CourseDAOImpl;
import database.daoimpl.EventDAOImpl;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import resources.Course;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class CourseServiceTest {
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String BASE_URL = "http://localhost:8080/api/softskills/courses";
    private static CourseDAOImpl courseDatabase = new CourseDAOImpl("courses", Course.class);
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
    public void createCourseTest() {
        try {
            testCourse = new Course("Testcourse", "A test course for JUnit", 50);
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 201) {
                fail("Wrong response code.");
            } else {
                assertTrue(response.header("Location").contains("http://localhost:8080/api/softskills/courses/"
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
    public void getAllCoursesTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code.");
            } else {
                assertTrue(response.body().string().contains("Testcourse"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(3)
    public void getCourseByIdTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(response.body().string().contains("Testcourse"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(4)
    public void getCourseByNameTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/?courseName=" + testCourse.getCourseName())
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                fail("Wrong response code");
            } else {
                assertTrue(response.body().string().contains("Testcourse"));
            }
        } catch (NullPointerException e) {
            fail("No response body has been sent by the server");
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(5)
    public void updateCourseTest() {
        try {
            testCourse.setCourseName("TestcoursePutTest");
            RequestBody requestBody = RequestBody.create(builder.serialize(testCourse), JSON);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .put(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .build();

            response = client.newCall(request).execute();

            assertTrue(response.body().string().contains("TestcoursePutTest"));
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @Test
    @Order(6)
    public void deleteCourseTest() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .delete()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 204) {
                fail("Wrong response code");
            }

            request = new Request.Builder()
                    .url(BASE_URL + "/" + testCourse.getHashId())
                    .get()
                    .build();

            response = client.newCall(request).execute();


            assertFalse(response.body().string().contains("TestcoursePutTest"));
        } catch (IOException e) {
            fail("Call to the Server couldn't be made. Is the server not running?");
        }
    }

    @AfterAll
    public void tearDown() {
        List<Course> allCoursesByName = courseDatabase.getByName("Testcourse", 0, -1);
        if (allCoursesByName.size() != 0) {
            courseDatabase.delete(allCoursesByName.get(0).getHashId());
        }
    }
}