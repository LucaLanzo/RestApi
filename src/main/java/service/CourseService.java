package service;

import paging.Pagination;
import database.CourseDAO;
import database.MongoDAOImpl;
import org.bson.types.ObjectId;
import resources.Course;

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
    protected CourseDAO<Course> courseDatabase = new MongoDAOImpl<>("courses", Course.class);


    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllCoursesOrByName(@QueryParam("name") @DefaultValue("") String name,
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
        } else {
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
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById(@PathParam("id") String id) {
        // Get the course from the database
        Course course = courseDatabase.getById(id);
        // If no course has been found by that id return 404 else display course with header hyperlinks to next state
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
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
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse) {
        // If the hash value of the course object isn't a valid ObjectId-Hash-Value or the name is null return 400
        // The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newCourse.getHashId()) || newCourse.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // Insert the course into the database
        courseDatabase.insertInto(newCourse);
        // Set the new location URI using the hash value as an index
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newCourse.getHashId()).build();

        return Response.created(locationURI).build();
    }


    // Update a specific course
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("id") String id, Course updatedCourse) {
        // If the name is not set return 400. If the course to be updated can't be found return 404
        if (updatedCourse.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (courseDatabase.isNotInDatabase(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            // Give the new course the same hash as the old one
            updatedCourse.setHashId(id);
            // Update the course in the database
            courseDatabase.update(updatedCourse, id);
            // Set the new header hyperlink to the next state
            Link link = Link.fromUri(uriInfo.getAbsolutePath())
                    .rel("getSingleCourse").type("application/json")
                    .build();

            return Response.noContent().links(link).build();
        }
    }


    // Delete a specific course
    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam ("id") String id) {
        // If the course can't be found return 404
        if (courseDatabase.isNotInDatabase(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            // Delete the course from the database
            courseDatabase.delete(id);
            // Set the new header hyperlink to the next state
            Link link = Link.fromUri(uriInfo.getBaseUri() + "courses")
                    .rel("getAllCourses").type("application/json")
                    .build();

            return Response.noContent().links(link).build();
        }
    }
}
