package service;

import database.dao.EventDAO;
import database.daoimpl.CourseDAOImpl;
import org.glassfish.jersey.server.Uri;
import paging.Pagination;
import database.dao.CourseDAO;
import database.daoimpl.EventDAOImpl;
import org.bson.types.ObjectId;
import resources.Course;
import resources.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
                                          @QueryParam("size") @DefaultValue("10") int size) {
        // Pagination offset and size check
        int amountOfResources;
        if (name.equals("")) {
            amountOfResources = courseDatabase.getAmountOfResources();
        } else {
            amountOfResources = courseDatabase.getAmountOfResources(name);
        }
        size = Pagination.checkSize(size);
        offset = Pagination.checkOffset(offset, amountOfResources);

        // Get all courses or all courses by specific name from the database
        List<Course> allCourses;
        if (name.equals("")) {
            allCourses = courseDatabase.getAll(offset, size);
        } else {
            allCourses = courseDatabase.getByName(name, offset, size);
        }

        // If no courses have been found return 404 else display all courses
        if (allCourses.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("createNewCourse").type("application/json")
                .build();

        // Create previousPage, thisPage and nextPage links
        Link previousPage = Pagination.createPreviousPage(uriInfo, "previousPage", name, offset, size);
        Link thisPage = Pagination.createThisPage(uriInfo, "selfPage", name, offset, size);
        Link nextPage = Pagination.createNextPage(uriInfo, "nextPage", name, offset, size, amountOfResources);

        Link[] links = Pagination.getLinkArray(linkForPost, previousPage, thisPage, nextPage);

        return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {})
                .links(links)
                .header("totalAmountOfCourses", amountOfResources)
                .build();

    }


    // Get specific course by hash-value
    @GET
    @Path("{courseId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById(@PathParam("courseId") String courseId) {
        // Get the course from the database
        Course course = courseDatabase.getById(courseId);
        // If no course has been found by that id return 404 else display course with header hyperlinks to next state
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Link linkToPut = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("updateSingleCourse").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("deleteSingleCourse").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();

        return Response.ok(course).links(linkToPut, linkToDelete, linkToGetAll).build();
    }


    // Get events of a specific course
    @GET
    @Path("{courseId}/events")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllEventsOfSpecificCourse(@PathParam("courseId") String courseId,
                                                 @QueryParam("name") @DefaultValue("") String name,
                                                 @QueryParam("offset") @DefaultValue("0") int offset,
                                                 @QueryParam("size") @DefaultValue("10") int size) {
        // Build a uri for the course
        URI uriToCourse = uriInfo.getBaseUriBuilder().path("courses/" + courseId).build();;

        int amountOfResources = eventDatabase.getAmountOfResources(uriToCourse.toString());
        size = Pagination.checkSize(size);
        offset = Pagination.checkOffset(offset, amountOfResources);

        // Get all courses or all courses by specific name from the database
        List<Event> allEventsWithSpecificCourse =
                eventDatabase.getByAssociatedCourse(uriToCourse.toString(), offset, size);

        // If no courses have been found return 404 else display all courses
        if (allEventsWithSpecificCourse.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Link linkForPost = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("createNewEvent").type("application/json")
                .build();

        // Create previousPage, thisPage and nextPage links
        Link previousPage = Pagination.createPreviousPage(uriInfo, "previousPage", name, offset, size);
        Link thisPage = Pagination.createThisPage(uriInfo, "selfPage", name, offset, size);
        Link nextPage = Pagination.createNextPage(uriInfo, "nextPage", name, offset, size, amountOfResources);

        Link[] links = Pagination.getLinkArray(linkForPost, previousPage, thisPage, nextPage);

        return Response.ok(new GenericEntity<Collection<Event>>(allEventsWithSpecificCourse) {})
                .links(links)
                .header("totalAmountOfEvents", amountOfResources)
                .build();
    }


    // Get specific event by hash-value
    @GET
    @Path("/{courseId}/events/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSpecificEventFromSpecificCourse(@PathParam("courseId") String courseId,
                                                       @PathParam("eventId") String eventId) {
        // Get the event from the database
        Event event = eventDatabase.getById(eventId);
        // If no event has been found by that id return 404 else display event with header hyperlinks to next state
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Link linkToPut = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("updateSingleEvent").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("deleteSingleEvent").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "event")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.ok(event).links(linkToPut, linkToDelete, linkToGetAll).build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse) {
        // If the hash value of the course object isn't a valid ObjectId-Hash-Value or the name is null return 400
        // The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newCourse.getHashId()) || newCourse.getCourseName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        URI pathToCourseEvents = uriInfo.getBaseUriBuilder().path("courses/" + newCourse.getHashId()
                + "/events").build();
        newCourse.setEvents(pathToCourseEvents.toString());

        // Insert the course into the database
        courseDatabase.insertInto(newCourse);
        // Set the new location URI using the hash value as an index
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newCourse.getHashId()).build();

        return Response.created(locationURI).build();
    }


    // Update a specific course
    @PUT
    @Path("{courseId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("courseId") String courseId, Course updatedCourse) {
        // If the name is not set return 400. If the course to be updated can't be found return 404
        if (updatedCourse.getCourseName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (courseDatabase.isNotInDatabase(courseId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Give the new course the same hash as the old one
        updatedCourse.setHashId(courseId);

        URI pathToCourseEvents = uriInfo.getBaseUriBuilder().path("courses/" + updatedCourse.getHashId()
                + "/events").build();
        updatedCourse.setEvents(pathToCourseEvents.toString());

        // Update the course in the database
        courseDatabase.update(updatedCourse, courseId);
        // Set the new header hyperlink to the next state
        Link link = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("getSingleCourse").type("application/json")
                .build();

        return Response.noContent().links(link).build();
    }


    // Delete a specific course
    @DELETE
    @Path("{courseId}")
    public Response deleteCourse(@PathParam ("courseId") String courseId) {
        // If the course can't be found return 404
        if (courseDatabase.isNotInDatabase(courseId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Delete the course from the database
        courseDatabase.delete(courseId);
        // Set the new header hyperlink to the next state
        Link link = Link.fromUri(uriInfo.getBaseUri() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();

        return Response.noContent().links(link).build();
    }
}
