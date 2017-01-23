package no.ntnu.stud.minvakt.services;

/**
 * Created by Marit on 23.01.2017.
 */

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Date;

public class OvertimeServiceTest {
    private static OvertimeService overtimeService;
    private HttpServletRequest request;

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        overtimeService = new OvertimeService(request);
    }

    @Test
    public void getUnapprovedOvertimeTest(){

    }

    @Test
    public void setApprovedOvertime() throws Exception {

    }

    @Ignore
    public void setOvertime() throws Exception {

    }

    @Ignore
    public void getOvertime() throws Exception {

    }

}
