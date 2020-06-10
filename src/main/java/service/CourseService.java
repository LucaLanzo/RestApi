package service;

import com.owlike.genson.Genson;
import database.MongoOp;
import org.bson.Document;
import org.bson.types.ObjectId;
import ressources.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// TODO Check for all the stupid shit a user can do
// TODO Make sure to get status codes right (5th script CRUD)

@Path("courses")
public class CourseService {
    @Context
    protected UriInfo uriInfo;


    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllCoursesOrByName(@QueryParam("name") @DefaultValue("") String name) {
        if (name.equals("")) {
            Genson builder = new Genson();
            List<Document> allCoursesInDoc = MongoOp.getAll();
            // Convert from a list of documents to a list of courses that can be turned to XML
            List<Course> allCourses = new ArrayList<>();
            for(Document course : allCoursesInDoc) {
                allCourses.add(builder.deserialize(course.toJson(), Course.class));
            }
            System.out.println("1");
            return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {}).build();
        } else {
            Document course = MongoOp.getByName(name);
            return Response.ok(course).build();
        }
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById( @PathParam("id") String id) {
        Document foundCourse = MongoOp.getById(new ObjectId(id));
        String foundCourseInJson = foundCourse.toJson();

        return Response.ok(foundCourse).build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse) {
        Genson builder = new Genson();
        // Serialize the course to JSON and then to Document BSON representation
        Document newCourseInDoc = Document.parse(builder.serialize(newCourse));
        String id = MongoOp.insertInto(newCourseInDoc, newCourse.getName());

        URI locationURI = uriInfo.getAbsolutePathBuilder().path(id).build();

        return Response.created(locationURI).build();
    }


    // Update a specific course
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam ("id") String id, Course updatedCourse) {
        Genson builder = new Genson();
        // Serialize the course to JSON and then to Document BSON representation
        Document updatedCourseInDoc = Document.parse(builder.serialize(updatedCourse));

        MongoOp.updateCourse(updatedCourseInDoc, new ObjectId(id));

        return Response.noContent().build();
    }


    // Delete a specific course
    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam ("id") String id) {
        MongoOp.deleteCourse(new ObjectId(id));

        return Response.noContent().build();
    }
}
