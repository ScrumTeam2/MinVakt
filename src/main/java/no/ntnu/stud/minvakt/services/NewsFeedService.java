package no.ntnu.stud.minvakt.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

/**
 * Created by evend on 1/20/2017.
 */
public class NewsFeedService extends SecureService{

    public NewsFeedService(@Context HttpServletRequest request) {
        super(request);
    }



}
