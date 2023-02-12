/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.labelling;

import fr.w3blog.zpl.constant.ZebraFont;
import fr.w3blog.zpl.model.ZebraLabel;
import fr.w3blog.zpl.model.ZebraPrintException;
import fr.w3blog.zpl.model.ZebraUtils;
import fr.w3blog.zpl.model.element.ZebraBarCode39;
import fr.w3blog.zpl.model.element.ZebraText;
import functionaljavaa.parameter.Parameter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
/* import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
*/
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author Administrator
 */
public class ZPL {

    enum ContentType{TEXT, BARCODE39}
    public static StringBuilder zplDemoLabel(String ip, int port){    
        StringBuilder logMsg=new StringBuilder(0);
        logMsg.append("printing in ip:"+ip+":"+port).append(String.format("%n", ""));
        ZebraLabel zebraLabel = new ZebraLabel(912, 912);
        zebraLabel.setDefaultZebraFont(ZebraFont.ZEBRA_ZERO);

        zebraLabel.addElement(new ZebraText(10, 84, "Product:", 14));
        zebraLabel.addElement(new ZebraText(395, 85, "Camera", 14));

        zebraLabel.addElement(new ZebraText(10, 161, "CA201212AA", 14));

        //Add Code Bar 39
        zebraLabel.addElement(new ZebraBarCode39(10, 297, "CA201212AA", 118, 2, 2));

        zebraLabel.addElement(new ZebraText(10, 365, "Qté:", 11));
        zebraLabel.addElement(new ZebraText(180, 365, "3", 11));
        zebraLabel.addElement(new ZebraText(317, 365, "QA", 11));

        zebraLabel.addElement(new ZebraText(10, 520, "Ref log:", 11));
        zebraLabel.addElement(new ZebraText(180, 520, "0035", 11));
        zebraLabel.addElement(new ZebraText(10, 596, "Ref client:", 11));
        zebraLabel.addElement(new ZebraText(180, 599, "1234", 11));
        try {
            ZebraUtils.printZpl(zebraLabel, ip, port);
            logMsg.append("Printed successfully");
            return logMsg;
        } catch (ZebraPrintException ex) {
            Logger.getLogger(ZPL.class.getName()).log(Level.SEVERE, null, ex);
            logMsg.append("Error printing: "+ex.getMessage());
            return logMsg;
        }
    }
    public static void zplLabel(String ip, int port, Object[][] lblContent){
        try {
            ResourceBundle propValue = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);
            
            ZebraLabel zebraLabel = new ZebraLabel(Integer.valueOf(propValue.getString("zpl.labelling.widthDots")), Integer.valueOf(propValue.getString("zpl.labelling.heightDots")));
            zebraLabel.setDefaultZebraFont(ZebraFont.valueOf(propValue.getString("zpl.labelling.ZebraFont")));
            for (Object[] curCont:lblContent){            
                ContentType cType = null;
                try{
//                    zebraLabel.addElement(new ZebraText(10, 84, "Product:", 14));
//                    zebraLabel.addElement(new ZebraBarCode39(10, 297, "CA201212AA", 118, 2, 2));
                    cType = ContentType.valueOf(curCont[0].toString());
                    switch (cType){
                        case TEXT:
                            zebraLabel.addElement(new ZebraText(
                                (int) curCont[2], (int) curCont[3], 
                                curCont[1].toString(), (int) curCont[4]));
                            break;
                        case BARCODE39:
                            zebraLabel.addElement(new ZebraBarCode39((int) curCont[2], (int) curCont[3], 
                                Arrays.toString(lblContent[1]), (int) curCont[4], (int) curCont[5], (int) curCont[6] ));
                            break;
                        default:
                            break;
                    }                                                
                }catch(NumberFormatException e){
//                    LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
//                    return;                   
                }
            }
            String command = "curl -X POST https://postman-echo.com/post --data foo1=bar1&foo2=bar2";
            command="curl --get http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/ --data-urlencode \""
                    + zebraLabel.getZplCode()
                    + "\" > label.png";
/*
var request = HttpRequest.newBuilder(uri)
    .header("Accept", "application/pdf") // omit this line to get PNG images back
    .POST(BodyPublishers.ofString(zpl))
    .build();
var client = HttpClient.newHttpClient();
var response = client.send(request, BodyHandlers.ofByteArray());
var body = response.body();

if (response.statusCode() == 200) {
    var file = new File("label.pdf"); // change file name for PNG images
    Files.write(file.toPath(), body);
} else {
    var errorMessage = new String(body, StandardCharsets.UTF_8);
    System.out.println(errorMessage);
}            
*/
        Process process = Runtime.getRuntime().exec(command);
        URI ur=null;
        ur = new URI(command);
/*        HttpRequest request = HttpRequest.newBuilder(ur)
            .header("Accept", "application/pdf") // omit this line to get PNG images back
            .POST(BodyPublishers.ofString(command))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse response = null;
        response = client.send(request, BodyHandlers.ofByteArray()); */
        zebraLabel.getZplCode();
        ZebraUtils.printZpl(zebraLabel, ip, port);
        } catch (ZebraPrintException|URISyntaxException|IOException ex){                
            Logger.getLogger(ZPL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
// aca obtenemos la printer default  
PrintService printService = PrintServiceLookup.lookupDefaultPrintService();  
  
String zplCommand = "^XA\n" +  
"^FO10,0^ARN,11,7^FD SOME TEXT ^FS\n" +  
"^FO300,0^ARN,11,7^FD SOME VALUE ^FS\n" +  
"^FO10,35^ARN,11,7^FD SOME TEXT ^FS\n" +  
"^FO300,35^ARN,11,7^FD SOME VALUE ^FS\n" +  
"^FO10,70^ARN,11,7^FD SOME CODE ^FS\n" +  
"^FO10,115^ARN,11,7^BCN,60,Y,Y,N^FD 23749237439827 ^FS\n" +  
"^XZ";  
  
// convertimos el comando a bytes  
byte[] by = zplCommand.getBytes();  
DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;  
Doc doc = new SimpleDoc(by, flavor, null);  
  
// creamos el printjob  
DocPrintJob job = printService.createPrintJob();  
  
// imprimimos  

//job.print(doc, null); 

//http://labelary.com/viewer.html

}
