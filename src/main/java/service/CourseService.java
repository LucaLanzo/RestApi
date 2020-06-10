package service;


import com.owlike.genson.Genson;
import database.MongoOperations;
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
    protected MongoOperations courseDatabase = new MongoOperations("courses");

    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllCoursesOrByName(@QueryParam("name") @DefaultValue("") String name) {
        Genson builder = new Genson();
        if (name.equals("")) {

            List<Document> allCoursesInDoc = courseDatabase.getAll();
            // Convert from a list of documents to a list of courses that can be turned to XML
            List<Course> allCourses = new ArrayList<>();
            for(Document courseInDoc : allCoursesInDoc) {
                allCourses.add(builder.deserialize(courseInDoc.toJson(), Course.class));
            }

            return Response.ok(new GenericEntity<Collection<Course>>(allCourses) {}).build();
        } else {
            Document courseInDoc = courseDatabase.getByName(name);
            Course course = builder.deserialize(courseInDoc.toJson(), Course.class);

            return Response.ok(course).build();
        }
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById( @PathParam("id") String id) {
        Genson builder = new Genson();
        Document foundCourseInDoc = courseDatabase.getById(new ObjectId(id));
        Course course = builder.deserialize(foundCourseInDoc.toJson(), Course.class);

        return Response.ok(course).build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(Course newCourse) {
        Genson builder = new Genson();
        // Serialize the course to JSON and then to Document BSON representation
        Document newCourseInDoc = Document.parse(builder.serialize(newCourse));
        String id = courseDatabase.insertInto(newCourseInDoc, newCourse.getName());

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

        courseDatabase.updateCourse(updatedCourseInDoc, new ObjectId(id));

        return Response.noContent().build();
    }


    // Delete a specific course
    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam ("id") String id) {
        courseDatabase.deleteCourse(new ObjectId(id));

        return Response.noContent().build();
    }
}
