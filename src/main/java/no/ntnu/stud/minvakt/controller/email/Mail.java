package no.ntnu.stud.minvakt.controller.email;

import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public Mail() {
        sendMailConfirm();
    }

    /**
     * Sends an email to a specified recipient, with a specified title & message.
     * @param recipient email of recipient
     * @param title subject of email
     * @param mailMessage email message
     * @return True if success
     */
    public static boolean sendMail(String recipient, String title, String mailMessage) {
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
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject(title);
            m.setText(mailMessage);
            Transport.send(m);
            return true;
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Could not send mail", e);
            return false;
        }
    }

    /**
     * @param recipient
     * @param title
     * @param mailMessage
     * @return
     */
    /**
     * Sends an email to a specified recipient, with a specified title, message & attachment
     * @param recipient email of recipient
     * @param title subject of email
     * @param mailMessage email message
     * @param fileName Attachment name in mail
     * @param content The content of the attachment
     * @return True if success
     */
    public static boolean sendMailWithAttachment(String recipient, String title, String mailMessage, String fileName, String content) {
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
            MimeMessage m = new MimeMessage(session);
            m.setFrom(new InternetAddress(from));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject(title, "ISO-8859-1");

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(mailMessage, "ISO-8859-1");

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setFileName(fileName);
            attachmentBodyPart.setText(content, "ISO-8859-1");

            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(attachmentBodyPart);
            m.setContent(multipart);

            Transport.send(m);
            return true;
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Could not send mail", e);
            return false;
        }
    }

    public static void sendMailConfirm() {
        System.out.println("SENDING EMAIL");
        String email = "system.minvakt@gmail.com";
        String subject = "Subject MinVakt";
        String content = "TestEmail";
        sendMail(email, subject, content);
    }
}
