package api;

import com.owlike.genson.Genson;
import database.MongoOp;
import org.bson.Document;
import org.bson.types.ObjectId;
import ressources.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.List;

// TODO Check for all the stupid shit a user can do

@Path("courses")
public class CourseService {
    @Context
    UriInfo uriInfo;


    // Get all courses in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
    public Response getAllCourses() {
        Genson builder = new Genson();
        List<Document> allDocs = MongoOp.getAll();
        return Response.ok(new GenericEntity<Collection<Document>>(allDocs){}).build();
    }


    // Get specific course by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourseById( @PathParam("id") String id) {
        String foundCourse = MongoOp.getById(new ObjectId(id));
        return Response.ok(foundCourse).build();
    }


    // Create a new course
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createPerson(Course course) {
        Genson builder = new Genson();
        // Convert object to JSON and then to BSON
        Document doc = Document.parse(builder.serialize(course));
        String id = MongoOp.insertInto(doc);

        URI locationURI = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(locationURI).build();
    }
}
