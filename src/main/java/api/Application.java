package api;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("softskills")
public class Application extends ResourceConfig {
    public Application() {
        super();
        registerClasses(getServiceClasses());
    }

    public Set<Class<?>> getServiceClasses() {
        Set<Class<?>> serviceClasses = new HashSet<>();
        serviceClasses.add(CourseService.class);
        return serviceClasses;
    }
}
