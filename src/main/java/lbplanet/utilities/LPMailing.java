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
    
    static final String FROM = "info.fran.gomez@gmail.com";
    static final String FROMNAME = "Sender Name";
	
    // Replace recipient@example.com with a "To" address. If your account 
    // is still in the sandbox, this address must be verified.
    static final String TO = "info.fran.gomez@gmail.com";
    
    // Replace smtp_username with your Amazon SES SMTP user name.
    static final String SMTP_USERNAME = "smtp_username";
    
    // Replace smtp_password with your Amazon SES SMTP password.
    static final String SMTP_PASSWORD = "smtp_password";
    
    // The name of the Configuration Set to use for this message.
    // If you comment out or remove this variable, you will also need to
    // comment out or remove the header below.
    static final String CONFIGSET = "ConfigSet";
    
    // Amazon SES SMTP host name. This example uses the EE.UU. Oeste (Oregón) region.
    // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
    // for more information.
    static final String HOST = "email-smtp.us-west-2.amazonaws.com";
    
    // The port you will connect to on the Amazon SES SMTP endpoint. 
    static final int PORT = 587;
    
    static final String SUBJECT = "Amazon SES test (SMTP interface accessed using Java)";
    
    static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>Amazon SES SMTP Email Test</h1>",
    	    "<p>This email was sent with Amazon SES using the ", 
    	    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
    	    " for <a href='https://www.java.com'>Java</a>."
    	);
    
public static void main(String[] args) {

        final String uname = "fgomezlw@gmail.com";
        final String pass = "Madrugada20.";

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
            message.setFrom(new InternetAddress("fgomezlw@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("info.fran.gomez@gmail.com")
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
            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                            }
            });    
            buildMailInternal(session, subject, body, toList, ccList, bccList, attachmentUrl);
    }

    public static void sendMailViaSSL(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
            
            ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            final String username = propValue.getString("ssl.mailuser");
            final String password = propValue.getString("ssl.mailpass");
            
            Properties prop = new Properties();
            prop.put("mail.smtp.host", propValue.getString("ssl.mail.smtp.host"));
            prop.put("mail.smtp.port", propValue.getString("ssl.mail.smtp.port"));
            prop.put("mail.smtp.auth", propValue.getString("ssl.mail.smtp.auth"));
            prop.put("mail.smtp.socketFactory.port", propValue.getString("ssl.mail.smtp.socketFactory.port")); 
            prop.put("mail.smtp.socketFactory.class", propValue.getString("ssl.mail.smtp.socketFactory.class")); 
            prop.put("mail.user", username);
            prop.put("mail.password", password); 
            
            Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                            }
            });    
            buildMailInternal(session, subject, body, toList, ccList, bccList, attachmentUrl);
    }
    
    private static void buildMailInternal(Session session, String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
      String to = "info.fran.gomez@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "fgomez@trazit.net";
      final String username = "fgomez";//change accordingly
      final String password = "fgomeztrazit123";//change accordingly

      // Assuming you are sending email through relay.jangosmtp.net
      String host = "mail.trazit.net";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "25");

      // Get the Session object.
      session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
	   }
         });

      try {
	   // Create a default MimeMessage object.
	   Message message = new MimeMessage(session);
	
	   // Set From: header field of the header.
	   message.setFrom(new InternetAddress(from));
	
	   // Set To: header field of the header.
	   message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(to));
	
	   // Set Subject: header field
	   message.setSubject("Testing Subject");
	
	   // Now set the actual message
	   message.setText("Hello, this is sample for to check send " +
		"email using JavaMailAPI ");

	   // Send message
	   Transport.send(message);

	   System.out.println("Sent message successfully....");

      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }


    }
    private static void buildMailInternal2(Session session, String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
        try {            
            ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            final String username = propValue.getString("tls.mailuser");            
            InternetAddress[] mailReceivers = new InternetAddress[toList.length];
            if (toList!=null && toList.length>0){
                int i=0;
                for (String curRecver: toList){
                    mailReceivers[i]=new InternetAddress(curRecver);
                    i=i+1;
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
            if (toList!=null && toList.length>0){
                if (attachmentUrl!=null){
                    for (String curAttach: attachmentUrl){            
                        MimeBodyPart adjunto = new MimeBodyPart();
                        adjunto.attachFile(curAttach);
                        multipart.addBodyPart(adjunto);
                    }    
                }
            }
            message.setContent(multipart);
            Transport.send(message);
            Logger.getLogger("Done");
        } catch (IOException|MessagingException ex) {
            Logger.getLogger(LPMailing.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }    
}      


