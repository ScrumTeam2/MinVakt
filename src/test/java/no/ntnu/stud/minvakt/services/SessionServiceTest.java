package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.util.ErrorInfo;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * Created by Audun on 11.01.2017.
 */
public class SessionServiceTest extends JerseyTest {

    @Override
    protected ResourceConfig configure() {
        return new ResourceConfig(SessionService.class);
    }

    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    public DeploymentContext configureDeployment() {
        return ServletDeploymentContext
                .forServlet(new ServletContainer(configure()))
                .build();
    }

    @Test
    public void checkLoginValidCredentials() throws Exception {
        //SessionService sessionService = new SessionService();
//        HttpServletRequest request = new MockHttpServletRequest();
//        Response response = sessionService.checkLogin(request, "email1", "password");

        Form form = new Form();
        form.param("identificator", "email1");
        form.param("password", "password");
        Response response = target("session/login").request().post(Entity.form(form));
        System.out.println(response);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        User user = response.readEntity(User.class); // Throws exception if not user
        Assert.assertFalse(user.getFirstName().isEmpty());
        Assert.assertFalse(user.getLastName().isEmpty());
        //Assert.assertFalse(user.getEmail().isEmpty());
        Assert.assertTrue(user.getId() > 0);

        //Assert.assertTrue(response.getEntity() instanceof User);
    }

    @Test
    public void checkLoginAlreadyLoggedIn() throws Exception {
        // We assume that this part work
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        sessionService.checkLogin(request, "email1", "password");

        // Try to login once more
        Response response = sessionService.checkLogin(request, "email1", "password");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        Assert.assertEquals(null, response.getEntity());
    }

    @Test
    public void checkLoginInvalidPassword() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "email1", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }

    @Test
    public void checkLoginInvalidCredentials() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "invalid", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }
}