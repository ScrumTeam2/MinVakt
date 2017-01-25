package no.ntnu.stud.minvakt.controller.mail;

import no.ntnu.stud.minvakt.controller.email.Mail;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/24/2017.
 */
public class MailControllerTest {
    @Test
    public void testEmailSend(){
        int result = Mail.sendMail("system.minvakt@gmail.com","Test","Dette er en test");
        assertTrue(result == 1);
    }
}
