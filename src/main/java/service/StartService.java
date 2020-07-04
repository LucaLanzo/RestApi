package service;

import authorization.Authorization;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Objects;

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
        String[] tokenAndRole = new String[2];
        try {
            tokenAndRole = Authorization.authorizeUser(authBody);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Link linkToCourses = Link.fromUri(uriInfo.getAbsolutePath() + "courses")
                .rel("getAllCourses").type("application/json")
                .build();
        Link linkToEvents = Link.fromUri(uriInfo.getAbsolutePath() + "events")
                .rel("getAllEvents").type("application/json")
                .build();

        if (tokenAndRole[0].equals("401")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=/api/softskills")
                    .build();
        }

        return Response.noContent().links(linkToCourses, linkToEvents)
                .header("Authorization", "Bearer " + tokenAndRole[0])
                .build();
    }
}
