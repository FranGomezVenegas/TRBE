/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lbplanet.utilities;

import databases.Rdbms;
import databases.TblsApp;
import javax.mail.*;
import javax.mail.internet.*;
import javax.net.ssl.*;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import module.instrumentsmanagement.definition.InstrumentsEnums;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.globalvariables.GlobalVariables;
import trazit.session.InternalMessage;
import java.security.cert.X509Certificate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
/**
 *
 * @author User
 */
/*
        final String username = ;
        final String password = ;

        Properties prop = new Properties();
        prop.put("mail.smtp.host", this.fldValues[LPArray.valuePosicInArray(fldNames, )]);
        prop.put("mail.smtp.port", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_PORT_TLS.getName())]);
        prop.put("mail.smtp.auth", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_AUTH.getName())]);
        prop.put("mail.smtp.starttls.enable", propValue.getString("tls.mail.smtp.starttls.enable")); //TLS
        prop.put("mail.smtp.ssl.trust", propValue.getString("tls.mail.smtp.ssl.trust"));
*/
public class LPMailing {
    
    String[] fldNames;
    Object[] fldValues;
    Boolean hasError;
    InternalMessage errorDetail;    
    
    public LPMailing() {
        Object[][] instrInfo = Rdbms.getRecordFieldsByFilter("", LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName()), TblsApp.TablesApp.MAILING.getTableName(),
                new String[]{TblsApp.Mailing.ACTIVE.getName()}, new Object[]{true}, getAllFieldNames(TblsApp.TablesApp.MAILING.getTableFields()));
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(instrInfo[0][0].toString())) {
            this.hasError = true;
            this.errorDetail = new InternalMessage(LPPlatform.LAB_FALSE, InstrumentsEnums.InstrumentsErrorTrapping.NOT_FOUND, new Object[]{"", TblsApp.TablesApp.MAILING.getTableName(), LPPlatform.buildSchemaName("", GlobalVariables.Schemas.APP.getName())}, "");
        } else {
            this.hasError = false;
            this.fldNames = getAllFieldNames(TblsApp.TablesApp.MAILING.getTableFields());
            this.fldValues = instrInfo[0];
        }
    }    
    
    public String sendEmail(String[] toList, String subject, String message, File[] attachFiles, Object jMainObj) {
        try {
            // Set properties for the mail session
            Properties properties = new Properties();
            properties.put("mail.smtp.host", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_HOST.getName())]);
            properties.put("mail.smtp.port", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_PORT_TLS.getName())]);
            properties.put("mail.smtp.auth", this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SMTP_AUTH.getName())]);
            properties.put("mail.smtp.starttls.enable", "true");

            final String username = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_USER.getName())].toString();
            final String password = this.fldValues[LPArray.valuePosicInArray(fldNames, TblsApp.Mailing.SENDER_PWD.getName())].toString();
            
            // Set up a TrustManager that trusts all certificates
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
                };

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
                properties.put("mail.smtp.ssl.socketFactory", sslSocketFactory);

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

            // Create a session with an authenticator
            Authenticator auth = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };
            Session session = Session.getInstance(properties, auth);

            // Create a new email message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(username));
            
            InternetAddress[] mailReceivers = new InternetAddress[toList.length];
            if (toList != null && toList.length > 0) {
                int i = 0;
                for (String curRecver : toList) {
                    mailReceivers[i] = new InternetAddress(curRecver);
                    i = i + 1;
                }
            }        
            msg.setRecipients(Message.RecipientType.TO, mailReceivers);
            msg.setSubject(subject);
            msg.setSentDate(new java.util.Date());

            // Create message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(message, "text/html");

            // Create multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Add attachments
            if (attachFiles != null && attachFiles.length > 0) {
                for (File attachFile : attachFiles) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    try {
                        attachPart.attachFile(attachFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    multipart.addBodyPart(attachPart);
                }
            }
           // Add JSON file as attachment
           if (jMainObj!=null){
                ObjectWriter ow = new ObjectMapper().writerWithDefaultPrettyPrinter();              
                String prettyJson = ow.writeValueAsString(jMainObj);
                MimeBodyPart jsonAttachmentPart = new MimeBodyPart();
                File jsonFile = new File("attachment.json");
                try (FileWriter fileWriter = new FileWriter(jsonFile)) {
                    fileWriter.write(prettyJson);
                }
                jsonAttachmentPart.attachFile(jsonFile);
                multipart.addBodyPart(jsonAttachmentPart);
           }
            // Set the multipart as the email content
            msg.setContent(multipart);

            // Send the email
            Transport.send(msg);
            return "sent!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }    
    
    public String zzzfakeMailForTesting(String[] mailTo, String subject, String message,File[] attachFiles, Object jMainObj) {
        // SMTP server configuration
        //String host = "smtp.example.com";
        //String port = "587";
        //String mailFrom = "your-email@example.com";
        //String password = "your-email-password";

        // Outgoing email information
        //String mailTo = "recipient@example.com";
        //String subject = "Subject of the email";
        //String message = "This is a test email with attachments";

        // Attach files
        //File[] attachFiles = new File[1];
        //attachFiles[0] = new File("/path/to/attachment");
        attachFiles=null;
        try {
            return sendEmail(mailTo, subject, message, attachFiles, jMainObj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Failed to send email.";
        }
    }
}
