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
        Link link = Link.fromUri(uriInfo.getAbsolutePath() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();

        return Response.noContent().links(link).build();
    }
}
