package service;

import authorization.Authorization;
import database.dao.EventDAO;
import paging.Pagination;
import database.daoimpl.EventDAOImpl;
import org.bson.types.ObjectId;
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


@Path("events")
public class EventService {
    @Context
    protected UriInfo uriInfo;
    protected EventDAO eventDatabase = new EventDAOImpl("events", Event.class);


    // Get all events in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllEvents(@QueryParam("offset") @DefaultValue("0") int offset,
                                  @QueryParam("size") @DefaultValue("10") int size,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        }

        // Get amount of events in the database
        int amountOfResources = eventDatabase.getAmountOfResources();

        // Get all courses or all courses by specific name from the database
        List<Event> allEvents = eventDatabase.getAll(offset, size);

        // If no courses have been found return 404 else display all courses
        if (allEvents.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Create POST and Pagination links
        Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("createNewEvent").type("application/json")
                .build();

        Link[] linksForPaginationAndPost = Pagination.createPagination(uriInfo, size, offset, amountOfResources, "",
                linkForPost);

        return Response.ok(new GenericEntity<Collection<Event>>(allEvents) {})
                .links(linksForPaginationAndPost)
                .header("X-totalAmountOfEvents", amountOfResources)
                .build();
    }


    // Get specific event by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEventById(@PathParam("id") String eventId,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
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
        Link linkToPut = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("updateSingleEvent").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("deleteSingleEvent").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "event")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.ok(event).links(linkToPut, linkToDelete, linkToGetAll)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Create a new event
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEvent(Event newEvent,
                                @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the hash value of the event object isn't a valid ObjectId-Hash-Value or the start/end times or date are
        // wrong return 400. The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newEvent.getHashId()) || newEvent.getStartTime() <= 0
                || newEvent.getEndTime() <= 0 || newEvent.getDate() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Set an absolute path on the course attribute of the new event
        URI pathToCourse = uriInfo.getBaseUriBuilder().path("courses/" + newEvent.getCourseId()).build();

        newEvent.setCourseId(pathToCourse.toString());

        // Insert the event into the database
        eventDatabase.insertInto(newEvent);

        // Set the new location URI using the hash value as an index
        URI locationURI = uriInfo.getAbsolutePathBuilder().path(newEvent.getHashId()).build();

        return Response.created(locationURI)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Update a specific event
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("id") String eventId, Event updatedEvent,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the name is not set return 400. If the event to be updated can't be found return 404
        if (updatedEvent.getStartTime() <= 0 || updatedEvent.getEndTime() <= 0 || updatedEvent.getDate() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        } else if (eventDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Give the new event the same hash as the old one
        updatedEvent.setHashId(eventId);

        // Update the event in the database
        eventDatabase.update(updatedEvent, eventId);

        // Create the GET link
        Link link = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("getSingleEvent").type("application/json")
                .build();

        return Response.noContent().links(link)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }


    // Delete a specific event
    @DELETE
    @Path("{id}")
    public Response deleteEvent(@PathParam("id") String eventId,
                                @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for authorization
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        } else if (tokenAndRole[1].equals("True")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the event can't be found return 404
        if (eventDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Delete the event from the database
        eventDatabase.delete(eventId);

        // Create the GET link
        Link link = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.noContent().links(link)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }
}
