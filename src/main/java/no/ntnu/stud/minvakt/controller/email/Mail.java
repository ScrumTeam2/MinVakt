package no.ntnu.stud.minvakt.controller.email;

import no.ntnu.stud.minvakt.jersey.MinVaktApp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mail {
    private static final String CHARSET = "ISO-8859-1";
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
        final String user = MinVaktApp.getAppProperties().getProperty("mail.user");
        final String pass = MinVaktApp.getAppProperties().getProperty("mail.password");

        Session session = Session.getInstance(MinVaktApp.getAppProperties(),
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });

        try {
            MimeMessage m = new MimeMessage(session);
            InternetAddress address = new InternetAddress(from);
            address.setPersonal("MinVakt", CHARSET);
            m.setFrom(address);
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject(title, CHARSET);
            m.setText(mailMessage, CHARSET);
            Transport.send(m);
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not send mail", e);
            return false;
        }
    }

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
        final String user = MinVaktApp.getAppProperties().getProperty("mail.user");
        final String pass = MinVaktApp.getAppProperties().getProperty("mail.password");

        Session session = Session.getInstance(MinVaktApp.getAppProperties(),
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, pass);
                    }
                });

        try {
            MimeMessage m = new MimeMessage(session);
            InternetAddress address = new InternetAddress(from);
            address.setPersonal("MinVakt", CHARSET);
            m.setFrom(address);
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject(title, CHARSET);

            Multipart multipart = new MimeMultipart();

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(mailMessage, CHARSET);

            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            attachmentBodyPart.setFileName(fileName);
            attachmentBodyPart.setText(content, CHARSET);

            multipart.addBodyPart(textBodyPart);
            multipart.addBodyPart(attachmentBodyPart);
            m.setContent(multipart);

            Transport.send(m);
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not send mail", e);
            return false;
        }
    }

    public static void sendMailConfirm() {
        String email = "system.minvakt@gmail.com";
        String subject = "Subject MinVakt";
        String content = "TestEmail";
        sendMail(email, subject, content);
    }
}
