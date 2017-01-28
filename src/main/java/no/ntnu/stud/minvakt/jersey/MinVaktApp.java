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

    private static Properties properties;

    @Context
    private ServletContext servletContext;

    static {
        // Load default config (used for unit tests)
        properties = new Properties();
        properties.setProperty("mysql.url", "jdbc:mysql://mysql.stud.iie.ntnu.no/g_scrum02");
        properties.setProperty("mysql.user", "g_scrum02");
        properties.setProperty("mysql.password", "82dvE5og");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", "smtp.googlemail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.user", "system.minvakt@gmail.com");
        properties.setProperty("mail.password", "m!v9#kTl");
    }

    private Properties readProperties() {
        properties = new Properties();
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
