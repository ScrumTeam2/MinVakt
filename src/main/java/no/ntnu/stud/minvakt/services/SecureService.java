package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

/**
 * Created by Audun on 10.01.2017.
 */
public abstract class SecureService {
    protected static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Context
    private HttpServletRequest request;

    protected Session getSession() throws NotAuthorizedException {
        // Tries to get session of logged in user. If no session, throw exception
        Session session = (Session)request.getSession().getAttribute("session");
        if(session == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return session;
    }
}
