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
        int amountOfResources = courseDatabase.getAmountOfResources();

        if (size <= 0) size = 1;
        if (offset >= amountOfResources) {
            offset = amountOfResources;
        }
        if (offset < 0) {
            offset = 0;
        }

        List<Course> allCourses;
        if (name.equals("")) {
            allCourses = courseDatabase.getAll(offset, size);
        } else {
            allCourses = courseDatabase.getByName(name, offset, size);
        }

        if (allCourses.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                    .rel("createNewCourse").type("application/json")
                    .build();

            Link previousPage = Pagination.createPreviousPage(uriInfo, "previousPage", name, offset, size);
            Link thisPage = Pagination.createThisPage(uriInfo, "selfPage", name, offset, size);
            Link nextPage = Pagination.createNextPage(uriInfo, "nextPage", name, offset, size, amountOfResources);

            Link[] links;
            if (previousPage == null && nextPage == null) {
                links = new Link[] {linkForPost, thisPage};
            } else if (previousPage == null) {
                links = new Link[] {linkForPost, thisPage, nextPage};
            } else if (nextPage == null) {
                links = new Link[] {linkForPost, previousPage, thisPage};
            } else {
                links = new Link[] {linkForPost, previousPage, thisPage, nextPage};
            }

            return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {})
                    .links(links)
                    .build();
        }
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById(@PathParam("id") String id) {
        Course course = courseDatabase.getById(id);
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
        if (!ObjectId.isValid(newCourse.getHashId()) || newCourse.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        courseDatabase.insertInto(newCourse);

        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newCourse.getHashId()).build();

        return Response.created(locationURI).build();
    }


    // Update a specific course
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("id") String id, Course updatedCourse) {
        if (updatedCourse.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (courseDatabase.isNotInDatabase(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            updatedCourse.setHashId(id);
            courseDatabase.update(updatedCourse, id);

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
        if (courseDatabase.isNotInDatabase(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            courseDatabase.delete(id);

            Link link = Link.fromUri(uriInfo.getBaseUri() + "courses")
                    .rel("getAllCourses").type("application/json")
                    .build();

            return Response.noContent().links(link).build();
        }
    }
}
