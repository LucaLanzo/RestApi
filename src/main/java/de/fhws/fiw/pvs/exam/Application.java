package de.fhws.fiw.pvs.exam;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import de.fhws.fiw.pvs.exam.service.CourseService;
import de.fhws.fiw.pvs.exam.service.EventService;
import de.fhws.fiw.pvs.exam.service.StartService;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;


@ApplicationPath("softskills")
public class Application extends ResourceConfig {
    public Application() {
        super();
        registerClasses(getServiceClasses());
        packages("org.glassfish.jersey.examples.linking");
        register(DeclarativeLinkingFeature.class);
    }

    public Set<Class<?>> getServiceClasses() {
        Set<Class<?>> serviceClasses = new HashSet<>();
        serviceClasses.add(StartService.class);
        serviceClasses.add(CourseService.class);
        serviceClasses.add(EventService.class);
        return serviceClasses;
    }
}
