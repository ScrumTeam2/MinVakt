package no.ntnu.stud.minvakt.jersey;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Audun on 27.01.2017.
 */
public class MinVaktApp extends Application {
    private static final String PROPERTIES_FILE = "/WEB-INF/MinVakt.properties";
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static Properties properties = new Properties();

    @Context
    private ServletContext servletContext;

    private Properties readProperties() {
        InputStream inputStream = servletContext.getResourceAsStream(PROPERTIES_FILE);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Could not detect/create app.properties", e);
            }
        }
        return properties;
    }

    @Override
    public Set<Class<?>> getClasses() {
        // Read the properties file
        readProperties();

        return super.getClasses();
    }

    public static Properties getAppProperties() {
        return properties;
    }
}
