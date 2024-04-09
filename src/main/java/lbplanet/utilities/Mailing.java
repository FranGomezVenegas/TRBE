/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import databases.Rdbms;
import databases.TblsApp;
import databases.TblsApp.TablesApp;
import functionaljavaa.parameter.Parameter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import module.instrumentsmanagement.definition.InstrumentsEnums.InstrumentsErrorTrapping;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;

/**
 *
 * @author User
 */
public class Mailing {

    static final String SUBJECT = "Amazon SES test (SMTP interface accessed using Java)";

    static final String BODY = String.join(
            System.getProperty("line.separator"),
            "<h1>Amazon SES SMTP Email Test</h1>",
            "<p>This email was sent with Amazon SES using the ",
            "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
            " for <a href='https://www.java.com'>Java</a>."
    );
    String[] fldNames;
    Object[] fldValues;
    Boolean hasError;
    InternalMessage errorDetail;    
    
    public void Mailing() {
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter("", LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName()), TablesApp.MAILING.getTableName(),
                new String[]{TblsApp.Mailing.ACTIVE.getName()}, new Object[]{true}, getAllFieldNames(TablesApp.MAILING.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) {
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsErrorTrapping.NOT_FOUND, new Object[]{"", TablesApp.MAILING.getTableName(), LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName())}, "");
        } else {
            this.hasError = false;
            this.fldNames = getAllFieldNames(TablesApp.MAILING.getTableFields());
            this.fldValues = instrInfo[0];
        }
    }

    
    public void sendMailViaTLS(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl, Integer port) {

        if (this.fldNames==null)return;
        ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        final String username = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_USER.getName())].toString();
        final String password = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_PWD.getName())].toString();

        Properties prop = new Properties();
        prop.put("mail.smtp.host", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_HOST.getName())]);
        prop.put("mail.smtp.port", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_PORT_TLS.getName())]);
        prop.put("mail.smtp.auth", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_AUTH.getName())]);
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

    public void sendMailViaSSL(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl, Integer port) {

        if (this.fldNames==null)return;
        ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
        final String username = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_USER.getName())].toString();
        final String password = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_PWD.getName())].toString();

        Properties prop = new Properties();
        prop.put("mail.smtp.host", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_HOST.getName())]);
        prop.put("mail.smtp.port", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_PORT_SSL.getName())]);
        prop.put("mail.smtp.auth", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_AUTH.getName())]);
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

    private void buildMailInternal2(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
//	   System.out.println("Sent message successfully....");
    }

    private void buildMailInternal3(Session session, String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
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
            if (attachmentUrl != null && toList != null && toList.length > 0) {
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
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    private void buildMailInternal(String subject, String body, String[] toList, String[] ccList, String[] bccList, String[] attachmentUrl) {
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
                    Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                            }
            });                
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, propValue.getString("tls.mail.from.alias")));
            message.setRecipients(Message.RecipientType.TO, mailReceivers);
            message.setSubject(subject);

            MimeBodyPart cuerpoCorreo = new MimeBodyPart();
            cuerpoCorreo.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpoCorreo);
            if (attachmentUrl != null && toList != null && toList.length > 0) {
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
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, null, ex.getMessage());
        }
    }

    public String mailAI(String hostArg, String portArg) {
        return "Host: " + hostArg + " port: " + portArg + "Sent message successfully....";
    }

}
