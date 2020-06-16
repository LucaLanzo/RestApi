package service;

import database.dao.CourseDAO;
import database.dao.EventDAO;
import database.daoimpl.CourseDAOImpl;
import paging.Pagination;
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


@Path("events")
public class EventService {
    @Context
    protected UriInfo uriInfo;
    protected CourseDAO<Course> courseDatabase = new CourseDAOImpl<>("courses", Course.class);
    protected EventDAO<Event> eventsDatabase = new EventDAOImpl<>("events", Event.class);


    // Get all events in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllEvents(@QueryParam("name") @DefaultValue("") String name,
                                  @QueryParam("offset") @DefaultValue("0") int offset,
                                  @QueryParam("size") @DefaultValue("10") int size) {
        // Pagination offset and size check
        int amountOfResources = amountOfResources = eventsDatabase.getAmountOfResources();
        size = Pagination.checkSize(size);
        offset = Pagination.checkOffset(offset, amountOfResources);

        // Get all courses or all courses by specific name from the database
        List<Event> allEvents = eventsDatabase.getAll(offset, size);

        // If no courses have been found return 404 else display all courses
        if (allEvents.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("createNewEvent").type("application/json")
                .build();

        // Create previousPage, thisPage and nextPage links
        Link previousPage = Pagination.createPreviousPage(uriInfo, "previousPage", name, offset, size);
        Link thisPage = Pagination.createThisPage(uriInfo, "selfPage", name, offset, size);
        Link nextPage = Pagination.createNextPage(uriInfo, "nextPage", name, offset, size, amountOfResources);

        Link[] links = Pagination.getLinkArray(linkForPost, previousPage, thisPage, nextPage);

        return Response.ok(new GenericEntity<Collection<Event>>(allEvents) {})
                .links(links)
                .header("totalAmountOfEvents", amountOfResources)
                .build();
    }


    // Get specific event by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEventById(@PathParam("id") String eventId) {
        // Get the event from the database
        Event event = eventsDatabase.getById(eventId);
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


    // Create a new event
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEvent(@QueryParam("courseId") String courseId, Event newEvent) {
        // If the hash value of the event object isn't a valid ObjectId-Hash-Value or the start/end times are null
        // return 400. The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newEvent.getHashId()) || newEvent.getStartTime() == 0
                || newEvent.getEndTime() == 0 || newEvent.getDate() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // Load the course from the database
        Course course = courseDatabase.getById(courseId);
        if (course == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // Set the course on the new event
        newEvent.setCourse(course);
        // Insert the event into the database
        eventsDatabase.insertInto(newEvent);
        // Set the new location URI using the hash value as an index
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newEvent.getHashId()).build();

        return Response.created(locationURI).build();
    }


    // Update a specific event
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("id") String eventId, Event updatedEvent) {
        // If the name is not set return 400. If the event to be updated can't be found return 404
        if (updatedEvent.getStartTime() == 0 || updatedEvent.getEndTime() == 0 || updatedEvent.getDate() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (eventsDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Give the new event the same hash as the old one
        updatedEvent.setHashId(eventId);
        // Update the event in the database
        eventsDatabase.update(updatedEvent, eventId);
        // Set the new header hyperlink to the next state
        Link link = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("getSingleEvent").type("application/json")
                .build();

        return Response.noContent().links(link).build();
    }


    // Delete a specific event
    @DELETE
    @Path("{id}")
    public Response deleteEvent(@PathParam("id") String eventId) {
        // If the event can't be found return 404
        if (eventsDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Delete the event from the database
        eventsDatabase.delete(eventId);
        // Set the new header hyperlink to the next state
        Link link = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.noContent().links(link).build();
    }
}
