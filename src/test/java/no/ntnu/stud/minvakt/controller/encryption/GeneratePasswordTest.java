package no.ntnu.stud.minvakt.controller.encryption;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Audun on 17.01.2017.
 */
public class GeneratePasswordTest {
    @Test
    public void generateRandomPass() throws Exception {
        String password = GeneratePassword.generateRandomPass();
        Assert.assertEquals(GeneratePassword.MAX_LENGTH, password.length());
    }
}