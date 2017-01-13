package no.ntnu.stud.minvakt.database;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;

/**
 * Created by evend on 1/13/2017.
 */
public class UserDBManagerTest {
    private static UserDBManager userDB;

    @BeforeClass
    public static void DBsetUp() {
        userDB = new UserDBManager();
    }
    @Ignore
    public void getUserBasics(){
        assertFalse(userDB.getUserBasics().isEmpty());
    }
}
