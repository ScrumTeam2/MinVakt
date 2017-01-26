package no.ntnu.stud.minvakt.services;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.shift.*;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasic;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ShiftServiceTest extends ServiceTest{
    private ShiftService shiftService;

    @Override
    public void setUp() {
        super.setUp();
        shiftService = new ShiftService(request);
    }

    @Test
    public void replaceEmployeeOnShift() {
        final int shiftId = 2;
        final int oldUserId = 1;
        final int newUserId = 15;

        logInAdmin();
        
        shiftService.addEmployeeToShift(oldUserId, shiftId);

        Response response = shiftService.replaceEmployeeOnShift(shiftId, oldUserId, newUserId);
        if(response.getStatus() == Response.Status.OK.getStatusCode()) {
            shiftService.deleteEmployeeFromShift(newUserId, shiftId, false);
        } else {
            shiftService.deleteEmployeeFromShift(oldUserId, shiftId, false);
        }

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void createShift() {
        logInUser();
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1, "Ole", User.UserCategory.HEALTH_WORKER, false,0, -1));
        Shift shift = new Shift(-1, 1, Date.valueOf("1995-10-23"), 1, 1, shiftUsers, false);
        Response response = shiftService.createShift(shift);
        if (response.getStatus() == 200) {
            String rawJson = (String) response.getEntity();
            JSONObject o = new JSONObject(rawJson);
            Integer shiftId = o.getInt("id");
            Response delResponse = shiftService.deleteShift(shiftId);
            assertTrue(delResponse.getStatus() == 200);
        }
        assertTrue(response.getStatus() == 200);
    }

    @Test
    public void getShift() {
        logInUser();
        assertNotNull(shiftService.getShift(1));
    }

    @Test
    public void addEmployeeToShift() {
        logInUser();

        Response statusOk = shiftService.addEmployeeToShift(1, 9);

        if (statusOk.getStatus() == 200) {
            statusOk = shiftService.deleteEmployeeFromShift(1, 9,false);
        }
        assertTrue(statusOk.getStatus() == 200);
    }

    @Test
    public void getShifts(){
        logInUser();
        ArrayList<ShiftUserAvailability> statusOk = shiftService.getShifts(300, new Date(System.currentTimeMillis()));
        assertFalse(statusOk.isEmpty());
    }

    @Test
    public void setStaffCount(){
        logInAdmin();
        Response response = shiftService.setStaffCount(-1,5);
        assertTrue(response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode());
        response = shiftService.setStaffCount(1,4);
        assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }
//    @Test
//    public void getPossibleCandidates() throws Exception {
//        logInUser();
//        Response response = shiftService.getPossibleCandidates(10);
//        ArrayList<UserBasicWorkHours> candidates = (ArrayList<UserBasicWorkHours>)response.getEntity();
//        Assert.assertTrue(candidates.size() > 0);
//    }
    @Test
    public void requestValidAbsence(){
        logInUser();
        Response response = shiftService.requestValidAbsence(1);
        assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }

    @Test
    public void getAvailableShiftsTest(){
        ArrayList<ShiftAvailable> statusOk = shiftService.getAvailableShifts();
        assertFalse(statusOk.isEmpty());
    }

    @Test
    public void requestShiftChange() throws Exception {
        logInUser();
        int shiftId = 43;

        Response response = shiftService.requestShiftChange(shiftId);
        int expResp = 200;
        Assert.assertEquals(expResp, response.getStatus());
    }

    @Test
    public void getUserBasicFromId() throws Exception {
        int userId = 1;
        ArrayList<ShiftUserBasic> shiftUserRes = shiftService.getUserBasicFromId(userId);
        //System.out.println(shiftUserRes.get(0).getShiftId());

        Assert.assertTrue(shiftUserRes.get(0) instanceof ShiftUserBasic);
    }

    @Test
    public void getUserBasicFromSession() throws Exception{
        logInUser();
        ArrayList<ShiftUserBasic> shiftUsers = shiftService.getUserBasicFromSession(Date.valueOf("2017-02-01"));
        Assert.assertTrue(shiftUsers.get(0) instanceof ShiftUserBasic);
    }
}
