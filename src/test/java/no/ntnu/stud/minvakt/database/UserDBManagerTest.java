package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.user.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
    public void isPhoneNumberTaken() {
        Assert.assertFalse(userDB.isPhoneNumberTaken("a"));
        Assert.assertTrue(userDB.isPhoneNumberTaken("phone1"));
    }

    @Test
    public void isEmailTaken() {
        Assert.assertFalse(userDB.isEmailTaken("a"));
        Assert.assertTrue(userDB.isEmailTaken("email1"));
    }

    @Test
    public void createAndDeleteNewUser() {
        Object[] userInfo = userDB.createNewUser("testFornavn", "testEtternavn", "system.minvakt@gmail.com", "10101010", User.UserCategory.ASSISTANT, 1,1);

        Assert.assertTrue((int)userInfo[0] > 0);

        User user = userDB.getUserById((int)userInfo[0]);
        Assert.assertTrue(userDB.deleteUserCompletely((int)userInfo[0]));

        Assert.assertEquals("testFornavn", user.getFirstName());
        Assert.assertEquals("testEtternavn", user.getLastName());
        Assert.assertEquals("system.minvakt@gmail.com", user.getEmail());
        Assert.assertEquals("10101010", user.getPhoneNumber());
        Assert.assertEquals(User.UserCategory.ASSISTANT, user.getCategory());
    }

    @Test
    public void createNewUserDuplicateMail() {
        Object[] userInfo1 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicateMail@gmail.com", "01010101", User.UserCategory.ASSISTANT, 1,1);

        Assert.assertTrue((int)userInfo1[0] > 0);

        Object[] userInfo2 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicateMail@gmail.com", "11111111", User.UserCategory.ASSISTANT, 1,1);
        Assert.assertTrue(userDB.deleteUserCompletely((int)userInfo1[0]));
        Assert.assertEquals(-1, userInfo2[0]);
    }

    @Test
    public void createNewUserDuplicatePhone() {
        Object[] userInfo1 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicatePhone@gmail.com", "11001100", User.UserCategory.ASSISTANT, 1,1);

        Assert.assertTrue((int)userInfo1[0] > 0);
        Object[] userInfo2 = userDB.createNewUser("testFornavn", "testEtternavn", "createNewUserDuplicatePhone2@gmail.com", "11001100", User.UserCategory.ASSISTANT, 1,1);
        Assert.assertTrue(userDB.deleteUserCompletely((int)userInfo1[0]));
        Assert.assertEquals(-1, userInfo2[0]);
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
        final int userId = 1;
        final String password = "password";
        assertTrue(userDB.checkLoginId(userId, password));
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
        ArrayList<User> users = userDB.getUsers(true);
        /*boolean successGetUsers = false;
        if(users.size() >0) {
            successGetUsers = true;
        }assertTrue(successGetUsers);
        */


        assertTrue(users.get(0) instanceof User);
    }

    @Test
    public void getUsersNoAdmins() {
        ArrayList<User> users = userDB.getUsers(false);
        for(User user : users) {
            Assert.assertNotEquals(User.UserCategory.ADMIN, user.getCategory());
        }

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
    @Test
    public void getUserBasicsFromCategory(){
        assertFalse(userDB.getUserBasicsWithCategory(User.UserCategory.ASSISTANT).isEmpty());
    }

    @Test
    public void getAdminId(){
        assertTrue(userDB.getAdminId() != 0);
    }

    @Test
    public void setNewPassword() {
        final int userId = 1;
        final String password = "password";
        Encryption e = new Encryption();
        String[] hashSalt = e.passEncoding(password);
        Assert.assertTrue(userDB.setNewPassword(userId, hashSalt));

        Assert.assertTrue(userDB.checkLoginId(userId, password));
    }
}