package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.User;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class UserDBManagerTest {
    private static UserDBManager userDB;

    @BeforeClass
    public static void DBsetUp() {
        try {
            userDB = new UserDBManager();
        } catch (Exception e) {
            System.err.println("Issue with DB Connection");
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void createNewUser() {
        int status= userDB.createNewUser("testFornavn", "testEtternavn", "testEmail@gmail.com", "12345678", "1");
        boolean successCreation = false;
        if(status>0) {
            successCreation = true;
        }
        assertTrue(successCreation);
    }
    
    @Ignore
    public void loginUser() {
        String username = "testUser@gmail.com";
        String password = "testPass";
        User userObj = userDB.loginUser(username,password);
        boolean successLogin = false;
        if (userObj != null) {
            successLogin = true;
            String emailLogin = userObj.getEmail();
        }
        //assertEquals("testUser@gmail.com",emailLogin);
        assertTrue(successLogin);
    }
    
    @Ignore
    public void loginUserId() {
        String userId = "1";
        int status = userDB.checkLoginId(userId, "testPass");
        boolean successLogin = false;
        if(status>0) {
            successLogin = true;
        }
        assertTrue(successLogin);
    }
    
    @Ignore
    public void getUser() {
        String userId = "1";
        User obtainedUser = userDB.getUserById(userId);
        boolean successObtain = false;
        if(obtainedUser != null) {
            successObtain = true;
        }
        assertTrue(successObtain);
    }
    
    
    @Ignore
    public void getUsers() {
        ArrayList<User> users = userDB.getUsers();
        /*boolean successGetUsers = false;
        if(users.size() >0) {
            successGetUsers = true;
        }assertTrue(successGetUsers);
        */
   
        assertTrue(users.get(0) instanceof User);
    }
    

    @Ignore
    public void changePassword() {
        String userId = "1";
        String prevPassword = "testPass";
        String newPass = "testPass";
        int status = userDB.changePasswordUserId(userId, prevPassword, newPass);
        boolean successChange = false;
        if(status>0) {
            successChange = true;
        }
        assertTrue(successChange);
    }

    public static void main(String[] args) {
        UserDBManagerTest udb = new UserDBManagerTest();
        udb.changePassword();
    }

}