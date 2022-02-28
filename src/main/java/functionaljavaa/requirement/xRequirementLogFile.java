/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.requirement;

import lbplanet.utilities.LPFrontEnd;
import functionaljavaa.parameter.Parameter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import lbplanet.utilities.LPNulls;

/**
 *
 * @author Administrator
 */
public class xRequirementLogFile {
    private xRequirementLogFile(){    throw new IllegalStateException("Utility class");}    
    
    static final void xrequirementLogFileNew(String procedureName){
        String newLogFileName = "Requirements.txt";        
        ResourceBundle prop = ResourceBundle.getBundle(Parameter.BUNDLE_TAG_PARAMETER_CONFIG_CONF);        
        String logDir = prop.getString("logDirPath");

        String logFile = logDir + "/" + newLogFileName;
        logFile=LPNulls.replaceNull(logFile);
        logFile=logFile.replace("/", "\\");        
    }

    /**
     *
     * @param logFile
     * @param functionName
     * @param entryValue
     * @param numTabs
     */
    public static final void xrequirementsLogEntry(String logFile, String functionName, String entryValue, Integer numTabs){ 
        String methodName = ""; //ºApiMessageReturn.getClassMethodName();
        FileWriter fw = null;  
        try{
            fw = new FileWriter(logFile, true);                  
            StringBuilder newEntryBuilder = new StringBuilder(0);
            if (numTabs!=null){
                for (Integer i=0;i<numTabs;i++){
                    newEntryBuilder.append( "     ");
                }
            }
            newEntryBuilder.append(methodName).append(": ").append(entryValue).append("\n");            
            fw.append(newEntryBuilder);

            fw.close();        
        }catch(IOException e){ 
            java.util.logging.Logger.getLogger(LPFrontEnd.class.getName()).log(Level.SEVERE, null, e);
        }
    }    
    
}
