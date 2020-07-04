package api;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/***
 * By Luca Lanzo
 */


public class Start {
    private static final String CONTEXT_PATH = "/api";
    private static final String WEB_APP_LOCATION = "src/main/webapp/";
    private static final String WEB_APP_MOUNT = "/WEB-INF/classes";
    private static final String WEB_APP_CLASSES = "target/classes";

    public static void main(String[] args) throws Exception {
        System.out.println("Server starting...");
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addWebapp(CONTEXT_PATH, new File(WEB_APP_LOCATION).getAbsolutePath());
        String pathToClasses = new File(WEB_APP_CLASSES).getAbsolutePath();
        WebResourceRoot resources = new StandardRoot(context);
        DirResourceSet dirResourceSet = new DirResourceSet(resources, WEB_APP_MOUNT, pathToClasses, "/");

        resources.addPreResources(dirResourceSet);
        context.setResources(resources);

        tomcat.start();
        System.out.println("Server started at Port:8080");
        tomcat.getServer().await();
    }
}