package no.ntnu.stud.minvakt.controller.email;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
    /*
     * Sends an email to a specified recipient, with a specified title & message.
     * @param recipient email of recipient
     * @param title subject of email
     * @param mailMessage email message
     * @return       1 if success
     */

    public Mail() {
        sendMailConfirm();
    }

    public static int sendMail(String recipient, String title, String mailMessage) {
        int error = 0;
        String to = recipient;
        String from = "system.minvakt@gmail.com";
        final String user = "system.minvakt@gmail.com";
        final String pass = "m!v9#kTl";

        String hoster = "smtp.googlemail.com";
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", hoster);
        p.put("mail.smtp.port", "587"); //enten 587 eller 25 dersom en av de er blokkert

        Session session = Session.getInstance(p,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });

        try {
            Message m = new MimeMessage(session);
            m.setFrom(new InternetAddress(from));
            m.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            m.setSubject(title);
            m.setText(mailMessage);
            Transport.send(m);
            error++;
        } catch (MessagingException e) {
            error--;
            throw new RuntimeException(e);
        }
        return error;
    }

    public static void sendMailConfirm() {
        System.out.println("SENDING EMAIL");
        String email = "system.minvakt@gmail.com";
        String subject = "Subject MinVakt";
        String content = "TestEmail";
        sendMail(email, subject, content);
    }
}
