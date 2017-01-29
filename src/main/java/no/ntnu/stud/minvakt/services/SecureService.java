package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * Abstract base class for all Service-classes that uses authentication
 */
public abstract class SecureService {
    protected static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private HttpServletRequest request;

    public SecureService(@Context HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Tries to get session of logged in user. If no session, throw exception
     * @return The session object of the logged in user
     * @throws NotAuthorizedException If no session
     */
    public Session getSession() throws NotAuthorizedException {
        Session session = (Session)request.getSession().getAttribute("session");
        if(session == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return session;
    }
}
