package de.fhws.fiw.pvs.exam.service;

import de.fhws.fiw.pvs.exam.authorization.Authorization;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;

/***
 * By Luca Lanzo
 */


@Path("")
public class StartService {
    @Context
    protected UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDispatcher(@HeaderParam("Authorization") @DefaultValue("") String authBody) {
        String[] tokenAndRole = new String[3];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Link linkToCourses = Link.fromUri(uriInfo.getAbsolutePath() + "/courses")
                .rel("getAllCourses").type("application/json")
                .build();
        Link linkToEvents = Link.fromUri(uriInfo.getAbsolutePath() + "/events")
                .rel("getAllEvents").type("application/json")
                .build();

        if (tokenAndRole[0].equals("401")) {
            return Authorization.getWWWAuthenticateResponse("realm=api/softskills/");
        }

        return Response.noContent().links(linkToCourses, linkToEvents)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }
}
