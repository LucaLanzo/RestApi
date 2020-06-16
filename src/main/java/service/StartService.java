package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/***
 * By Luca Lanzo
 */


@Path("")
public class StartService {
    @Context
    protected UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDispatcher() {
        Link linkToCourses = Link.fromUri(uriInfo.getAbsolutePath() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();
        Link linkToEvents = Link.fromUri(uriInfo.getAbsolutePath() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        return Response.noContent().links(linkToCourses, linkToEvents).build();
    }
}
