package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Audun on 10.01.2017.
 */
public abstract class SecureService {
    @Context
    private HttpServletRequest request;

    protected Session getSession() throws NotAuthorizedException {
        // Tries to get session of logged in user. If no session, throw exception
        Session session = (Session)request.getSession().getAttribute("session");
        if(session == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.FORBIDDEN);
        }
        return session;
    }
}
