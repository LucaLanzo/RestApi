package de.fhws.fiw.pvs.exam.service;

import de.fhws.fiw.pvs.exam.authorization.Authorization;
import de.fhws.fiw.pvs.exam.database.DAOFactory;
import de.fhws.fiw.pvs.exam.database.dao.EventDAO;
import de.fhws.fiw.pvs.exam.paging.Pagination;
import org.bson.types.ObjectId;
import de.fhws.fiw.pvs.exam.resources.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/***
 * By Luca Lanzo
 */


@Path("events")
public class EventService {
    @Context
    protected UriInfo uriInfo;
    protected EventDAO eventDatabase = DAOFactory.createEventDAO();


    // Get all events in the database
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllEvents(@QueryParam("from") @DefaultValue("") String startTime,
                                 @QueryParam("to") @DefaultValue("") String endTime,
                                 @QueryParam("offset") @DefaultValue("0") int offset,
                                 @QueryParam("size") @DefaultValue("10") int size,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for de.fhws.fiw.pvs.exam.authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        }

        List<Event> allEvents;
        if (eventDatabase.startIsAfterEndOrWrongFormat(startTime, endTime)) {
            allEvents = new ArrayList<>();
        } else if (startTime.equals("") && endTime.equals("")) {
            allEvents = eventDatabase.getAll(offset, size);
        } else if (startTime.equals("")) {
            allEvents = eventDatabase.getByEndTime(endTime, offset, size);
        } else if (endTime.equals("")) {
            allEvents = eventDatabase.getByStartTime(startTime, offset, size);
        } else {
            allEvents = eventDatabase.getByTimeframe(startTime, endTime, offset, size);
        }

        // If the offset is bigger than the amount of events, return an empty list
        if (offset > allEvents.size()) {
            allEvents = new ArrayList<>();
        }

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(60);
        cacheControl.setPrivate(true);

        // Create POST and Pagination links
        Link linkForPost = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("createNewEvent").type("application/json")
                .build();

        Link[] linksForPaginationAndPost = Pagination.createPagination(uriInfo, size, offset, allEvents.size(), "",
                linkForPost);


        return Response.ok(new GenericEntity<Collection<Event>>(allEvents) {})
                .links(linksForPaginationAndPost)
                .header("X-totalAmountOfEvents", allEvents.size())
                .cacheControl(cacheControl)
                .build();
    }


    // Get specific event by hash-value
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEventById(@Context Request request,
                                 @PathParam("id") String eventId,
                                 @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for de.fhws.fiw.pvs.exam.authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        }

        // Get the event from the database
        Event event = eventDatabase.getById(eventId);

        // If no event has been found by that id return 404 else display event with header hyperlinks to next state
        if (eventDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(60);
        cacheControl.setPrivate(true);

        // Create PUT, DELETE and GET links
        Link linkToPut = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("updateSingleEvent").type("application/json")
                .build();
        Link linkToDelete = Link.fromUri(uriInfo.getAbsolutePath())
                .rel("deleteSingleEvent").type("application/json")
                .build();
        Link linkToGetAll = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.ok(event).links(linkToPut, linkToDelete, linkToGetAll)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .cacheControl(cacheControl)
                .build();
    }


    // Create a new event
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createEvent(Event newEvent,
                                @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for de.fhws.fiw.pvs.exam.authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        } else if (tokenAndRole[1].equals("student")) {
            return Authorization.getWrongRoleResponse();
        }

        // If the hash value of the event object isn't a valid ObjectId-Hash-Value or the start/end times or date are
        // wrong return 400. The resource will automatically create a hash if it hasn't been set by the client
        if (!ObjectId.isValid(newEvent.getHashId()) || newEvent.getStartTime() == null
                || newEvent.getEndTime() == null ||
                eventDatabase.startIsAfterEndOrWrongFormat(newEvent.getStartTime(), newEvent.getEndTime())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // Set an absolute path on the course attribute of the new event
        URI pathToCourse = uriInfo.getBaseUriBuilder().path("courses/" + newEvent.getCourseId()).build();

        newEvent.setCourseId(pathToCourse.toString());
        if (newEvent.getSignedUpStudents() == null) {
            newEvent.setSignedUpStudents(new HashSet<>());
        }

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
    public Response updateEvent(@Context Request request,
                                @PathParam("id") String eventId, Event updatedEvent,
                                @HeaderParam("Authorization") @DefaultValue("") String authBody) {
        // Check for de.fhws.fiw.pvs.exam.authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        }

        // If the name is not set return 400. If the event to be updated can't be found return 404
        if (updatedEvent.getStartTime() == null || updatedEvent.getEndTime() == null ||
                eventDatabase.startIsAfterEndOrWrongFormat(updatedEvent.getStartTime(), updatedEvent.getEndTime())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        } else if (eventDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // if client is a student, sign him up with his cn
        if (tokenAndRole[1].equals("student")) {
            eventDatabase.signUp(tokenAndRole[2], eventId);
        } else {
            // Update the event in the database
            eventDatabase.update(updatedEvent, eventId);
        }

        // I would love to do a conditional PUT with eTags here, but as mongodb inserts and extracts the POJO's from
        // the database the hashvalues change. I can't verify the eTags as they are not consistent.

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
        // Check for de.fhws.fiw.pvs.exam.authorization
        String[] tokenAndRole = authorizeUser(authBody);

        // Exit with WWW-Authenticate if wrong creds have been sent
        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("api/softskills/events");
        }

        // If the event can't be found return 404
        if (eventDatabase.isNotInDatabase(eventId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Authorization", "Bearer " + tokenAndRole[0])
                    .build();
        }

        // if client is a student, remove his cn from the event
        if (tokenAndRole[1].equals("student")) {
            eventDatabase.leave(tokenAndRole[2], eventId);
        } else {
            // Delete the event from the database
            eventDatabase.delete(eventId);
        }

        // Create the GET link
        Link link = Link.fromUri(uriInfo.getBaseUri() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.noContent().links(link)
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
