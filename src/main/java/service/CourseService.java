package service;

import authorization.Authorization;
import database.dao.EventDAO;
import database.daoimpl.CourseDAOImpl;
import paging.Pagination;
import database.dao.CourseDAO;
import database.daoimpl.EventDAOImpl;
import org.bson.types.ObjectId;
import resources.Course;
import resources.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;

import java.util.Collection;
import java.util.List;

/***
 * By Luca Lanzo
 */


@Path("courses")
public class CourseService {
    @Context
    protected UriInfo uriInfo;
    protected CourseDAO courseDatabase = new CourseDAOImpl("courses", Course.class);
    protected EventDAO eventDatabase = new EventDAOImpl("events", Event.class);


    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllCourses(@QueryParam("courseName") @DefaultValue("") String name,
                                  @QueryParam("offset") @DefaultValue("0") int offset,
                                  @QueryParam("size") @DefaultValue("10") int size,
                                  @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        }

        // Get all courses or all courses by specific name from the database
        List<Course> allCourses;
        if (name.equals("")) allCourses = courseDatabase.getAll(offset, size);
        else allCourses = courseDatabase.getByName(name, offset, size);

        // If no courses have been found return 404 else display all courses
        if (allCourses.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Create the POST and Pagination links
        Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("createNewCourse").type("application/json")
                .build();
        Link[] linksForPaginationAndPost = Pagination.createPagination(uriInfo, size, offset, allCourses.size(), name,
                linkForPost);

        return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {})
                .links(linksForPaginationAndPost)
                .header("X-totalAmountOfCourses", allCourses.size())
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Get specific course by hash-value
    @GET
    @Path("{courseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById(@PathParam("courseId") String courseId,
                                  @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        }

        // Get the course from the database
        Course course = courseDatabase.getById(courseId);

        // If no course has been found by that id return 404 else display course with header hyperlinks to next state
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Create the PUT, DELETE and GET links
        Link linkToPut = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("updateSingleCourse").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("deleteSingleCourse").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();

        return Response.ok(course).links(linkToPut, linkToDelete, linkToGetAll)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Get events of a specific course
    @GET
    @Path("{courseId}/events")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllEventsOfSpecificCourse(@PathParam("courseId") String courseId,
                                                 @QueryParam("from") @DefaultValue("") String startTime,
                                                 @QueryParam("to") @DefaultValue("") String endTime,
                                                 @QueryParam("offset") @DefaultValue("0") int offset,
                                                 @QueryParam("size") @DefaultValue("10") int size,
                                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        }

        // Build a uri for the course for searching
        URI uriToCourse = uriInfo.getBaseUriBuilder().path("courses/" + courseId).build();

        // Get all events for the chosen course
        List<Event> allEventsWithSpecificCourse;
        if (startTime.equals("") && endTime.equals("")) {
            allEventsWithSpecificCourse = eventDatabase.getAll(offset, size);
        } else if (startTime.equals("")) {
            allEventsWithSpecificCourse = eventDatabase.getByStartTime(startTime, offset, size);
        } else if (endTime.equals("")) {
            allEventsWithSpecificCourse = eventDatabase.getByEndTime(endTime, offset, size);
        } else {
            allEventsWithSpecificCourse = eventDatabase.getByTimeframe(startTime, endTime, uriToCourse.toString(), offset, size);
        }

        // If no events have been found return 404 else display all events
        if (allEventsWithSpecificCourse.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Create the POST and Pagination links
        Link linkForPost = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("createNewEvent").type("application/json")
                .build();
        Link[] linksForPaginationAndPost = Pagination.createPagination(uriInfo, size, offset,
                allEventsWithSpecificCourse.size(), "", linkForPost);

        return Response.ok(new GenericEntity<Collection<Event>>(allEventsWithSpecificCourse) {})
                .links(linksForPaginationAndPost)
                .header("X-totalAmountOfEvents", allEventsWithSpecificCourse.size())
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Get specific event by hash-value
    @GET
    @Path("/{courseId}/events/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSpecificEventFromSpecificCourse(@PathParam("eventId") String eventId,
                                                       @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        }

        // Get the event from the database
        Event event = eventDatabase.getById(eventId);

        // If no event has been found by that id return 404 else display event with header hyperlinks to next state
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Create PUT, DELETE and GET links
        Link linkToPut = Link.fromUri(uriInfo.getBaseUri() + "events/" + event.getHashId())
                .rel("updateSingleEvent").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getBaseUri() + "events/" + event.getHashId())
                .rel("deleteSingleEvent").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.ok(event).links(linkToPut, linkToDelete, linkToGetAll)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent or exit with Forbidden if user is student
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the hash value of the course object isn't a valid ObjectId-Hash-Value or the name is null return 400
        // The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newCourse.getHashId()) || newCourse.getCourseName().equals("")
                || newCourse.getCourseDescription().equals("") || newCourse.getMaximumStudents() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Set the path to the course's events
        URI pathToCourseEvents = uriInfo.getBaseUriBuilder().path("courses/" + newCourse.getHashId()
                + "/events").build();
        newCourse.setEvents(pathToCourseEvents.toString());

        // Insert the course into the database
        courseDatabase.insertInto(newCourse);

        // Set the new location URI using the hash value as an index
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newCourse.getHashId()).build();

        return Response.created(locationURI)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Update a specific course
    @PUT
    @Path("{courseId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("courseId") String courseId, Course updatedCourse,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent or exit with Forbidden if user is student
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If all attributes of the updated course are default (= no changes) or maximumStudents is wrong return 400
        // If the course to be updated can't be found return 404
        if ((updatedCourse.getCourseName().equals("") && updatedCourse.getCourseName().equals("")
        && updatedCourse.getMaximumStudents() == 0) || updatedCourse.getMaximumStudents() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        } else if (courseDatabase.isNotInDatabase(courseId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Give the new course the same hash as the old one
        updatedCourse.setHashId(courseId);

        // Set the path to the course's events
        URI pathToCourseEvents = uriInfo.getBaseUriBuilder().path("courses/" + updatedCourse.getHashId()
                + "/events").build();
        updatedCourse.setEvents(pathToCourseEvents.toString());

        // Update the course in the database
        courseDatabase.update(updatedCourse, courseId);

        // Create the GET link
        Link linkToGet = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("getSingleCourse").type("application/json")
                .build();

        return Response.noContent().links(linkToGet)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Delete a specific course
    @DELETE
    @Path("{courseId}")
    public Response deleteCourse(@PathParam ("courseId") String courseId,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/courses");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the course can't be found return 404
        if (courseDatabase.isNotInDatabase(courseId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Delete the course from the database
        courseDatabase.delete(courseId);

        // Create the GET link
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();

        return Response.noContent().links(linkToGetAll)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    public static String[] authorizeUser(String authBody) {
        try {
            return Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // If there is an IOException return 401 to make sure the CRUDs block the request with a
        // WWW-Authenticate-Header response
        return new String[]{("401"), ("False")};
    }
}
