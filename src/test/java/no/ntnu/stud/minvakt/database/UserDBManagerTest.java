package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import static junit.framework.TestCase.assertFalse;

/**
 * Created by evend on 1/13/2017.
 */
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

    @Test
    public void createAndDeleteNewUser() {
        int userId = userDB.createNewUser("testFornavn", "testEtternavn", "createAndDeleteNewUser@gmail.com", "10101010", User.UserCategory.ASSISTANT.getValue());

        Assert.assertTrue(userId > 0);

        User user = userDB.getUserById(userId);
        Assert.assertTrue(userDB.deleteUser(userId));

        Assert.assertEquals("testFornavn", user.getFirstName());
        Assert.assertEquals("testEtternavn", user.getLastName());
        Assert.assertEquals("createAndDeleteNewUser@gmail.com", user.getEmail());
        Assert.assertEquals("10101010", user.getPhoneNumber());
        Assert.assertEquals(User.UserCategory.ASSISTANT, user.getCategory());
    }

    @Test
    public void createNewUserDuplicateMail() {
        int userId = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicateMail@gmail.com", "01010101", 1);

        Assert.assertTrue(userId > 0);

        int userId2 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicateMail@gmail.com", "11111111", 1);
        Assert.assertTrue(userDB.deleteUser(userId));
        Assert.assertEquals(-1, userId2);
    }

    @Test
    public void createNewUserDuplicatePhone() {
        int userId = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicatePhone@gmail.com", "11001100", 1);

        Assert.assertTrue(userId > 0);

        int userId2 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicatePhone2@gmail.com", "11001100", 1);
        Assert.assertTrue(userDB.deleteUser(userId));
        Assert.assertEquals(-1, userId2);
    }
    
    @Test
    public void loginUser() {
        String username = "email1";
        String password = "password";
        User userObj = userDB.loginUser(username,password);
        boolean successLogin = false;
        if (userObj != null) {
            successLogin = true;
            String emailLogin = userObj.getEmail();
        }
        //assertEquals("testUser@gmail.com",emailLogin);
        assertTrue(successLogin);
    }
    
    @Test
    public void loginUserId() {
        String userId = "1";
        int status = userDB.checkLoginId(userId, "password");
        boolean successLogin = false;
        if(status>0) {
            successLogin = true;
        }
        assertTrue(successLogin);
    }
    
    @Test
    public void getUser() {
        int userId = 1;
        User obtainedUser = userDB.getUserById(userId);
        boolean successObtain = false;
        if(obtainedUser != null) {
            successObtain = true;
        }
        assertTrue(successObtain);
    }
    
    
    @Test
    public void getUsers() {
        ArrayList<User> users = userDB.getUsers();
        /*boolean successGetUsers = false;
        if(users.size() >0) {
            successGetUsers = true;
        }assertTrue(successGetUsers);
        */

   
        assertTrue(users.get(0) instanceof User);
    }

    

    @Test
    public void changePassword() {
        String userId = "1";
        String prevPassword = "password";
        String newPass = "password";
        int status = userDB.changePasswordUserId(userId, prevPassword, newPass);
        boolean successChange = false;
        if(status>0) {
            successChange = true;
        }
        assertTrue(successChange);
    }

    @Test
    public void getUserBasics(){
        assertFalse(userDB.getUserBasics().isEmpty());
    }
}
