/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import functionaljavaa.parameter.Parameter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author User
 */
public class LPMailing {

    static final String SUBJECT = "Amazon SES test (SMTP interface accessed using Java)";

    static final String BODY = String.join(
            System.getProperty("line.separator"),
            "<h1>Amazon SES SMTP Email Test</h1>",
            "<p>This email was sent with Amazon SES using the ",
            "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
            " for <a href='https://www.java.com'>Java</a>."
    );

    public static void main(String[] args) {

        final String uname = "";
        final String pass = "";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, pass);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(""));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("")
            );
            message.setSubject("Testing Gmail main");
            message.setText("Dear Mail Crawler,"
                    + "\n\n Please do not spam my email!");

            Transport.send(message);

        } catch (MessagingException e) {
            Logger.getLogger(LPMailing.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void sendMailViaTLS(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {

        ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        final String username = propValue.getString("tls.mailuser");
        final String password = propValue.getString("tls.mailpass");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", propValue.getString("tls.mail.smtp.host"));
        prop.put("mail.smtp.port", propValue.getString("tls.mail.smtp.port"));
        prop.put("mail.smtp.auth", propValue.getString("tls.mail.smtp.auth"));
        prop.put("mail.smtp.starttls.enable", propValue.getString("tls.mail.smtp.starttls.enable")); //TLS
        prop.put("mail.smtp.ssl.trust", propValue.getString("tls.mail.smtp.ssl.trust"));
        prop.put("mail.user", username);
        prop.put("mail.password", password); //TLS
/*            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                            }
            });    */
        buildMailInternal(subject, body, toList, ccList, bccList, attachmentUrl);
    }

    public static void sendMailViaSSL(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {

        ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        final String username = propValue.getString("ssl.mailuser");
        final String password = propValue.getString("ssl.mailpass");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", propValue.getString("ssl.mail.smtp.host"));
        prop.put("mail.smtp.port", propValue.getString("ssl.mail.smtp.port"));
        prop.put("mail.smtp.auth", propValue.getString("ssl.mail.smtp.auth"));
        //prop.put("mail.smtp.socketFactory.port", propValue.getString("ssl.mail.smtp.socketFactory.port")); 
        //prop.put("mail.smtp.socketFactory.class", propValue.getString("ssl.mail.smtp.socketFactory.class")); 
        prop.put("javax.net.ssl.trustStoreType", "none");
        prop.put("mail.user", username);
        prop.put("mail.password", password);

        /*            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                            }
            });    */
        buildMailInternal(subject, body, toList, ccList, bccList, attachmentUrl);
    }

    private static void buildMailInternal(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
//	   System.out.println("Sent message successfully....");
    }

    private static void buildMailInternal2(Session session, String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
        try {
            ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            final String username = propValue.getString("tls.mailuser");
            InternetAddress[] mailReceivers = new InternetAddress[toList.length];
            if (toList != null && toList.length > 0) {
                int i = 0;
                for (String curRecver : toList) {
                    mailReceivers[i] = new InternetAddress(curRecver);
                    i = i + 1;
                }
            }
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, propValue.getString("tls.mail.from.alias")));
            message.setRecipients(Message.RecipientType.TO, mailReceivers);
            message.setSubject(subject);

            MimeBodyPart cuerpoCorreo = new MimeBodyPart();
            cuerpoCorreo.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpoCorreo);
            if (toList != null && toList.length > 0 && attachmentUrl != null) {
                for (String curAttach : attachmentUrl) {
                    MimeBodyPart adjunto = new MimeBodyPart();
                    adjunto.attachFile(curAttach);
                    multipart.addBodyPart(adjunto);
                }
            }
            message.setContent(multipart);
            Transport.send(message);
            Logger.getLogger("Done");
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(LPMailing.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    public static String mailAI(String hostArg, String portArg) {
        return "Host: " + hostArg + " port: " + portArg + "Sent message successfully....";
    }

}
