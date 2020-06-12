package service;

import database.MongoOperations;
import org.bson.types.ObjectId;
import ressources.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

import java.util.Collection;
import java.util.List;

/***
 * By Luca Lanzo
 */

// TODO Check for all the stupid shit a user can do
// To return header in response: Response.ok(bla).header("links", link).build();


@Path("courses")
public class CourseService {
    @Context
    protected UriInfo uriInfo;
    protected MongoOperations<Course> courseDatabase = new MongoOperations<>("courses", Course.class);


    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllCoursesOrByName(@QueryParam("name") @DefaultValue("") String name) {
        if (name.equals("")) {
            List<Course> allCourses = courseDatabase.getAll();
            return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {}).build();
        } else {
            Course course = courseDatabase.getByName(name);
            return Response.ok(course).build();
        }
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById( @PathParam("id") String id) {
        Course course = courseDatabase.getById(id);
        return Response.ok(course).build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse) {
        Course courseFromDatabase = courseDatabase.insertInto(newCourse, newCourse.getName());
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(courseFromDatabase.getHashId()).build();
        return Response.created(locationURI).build();
    }


    // Update a specific course
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("id") String id, Course updatedCourse) {
        updatedCourse.setHashId(id);
        courseDatabase.update(updatedCourse, id);
        return Response.noContent().build();
    }


    // Delete a specific course
    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam ("id") String id) {
        courseDatabase.delete(id);
        return Response.noContent().build();
    }
}
