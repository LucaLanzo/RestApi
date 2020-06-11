package api;

import org.glassfish.jersey.server.ResourceConfig;
import service.CourseService;
import service.StartService;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

/***
 * By Luca Lanzo
 */


@ApplicationPath("softskills")
public class Application extends ResourceConfig {
    public Application() {
        super();
        registerClasses(getServiceClasses());
    }

    public Set<Class<?>> getServiceClasses() {
        Set<Class<?>> serviceClasses = new HashSet<>();
        serviceClasses.add(StartService.class);
        serviceClasses.add(CourseService.class);
        return serviceClasses;
    }
}
