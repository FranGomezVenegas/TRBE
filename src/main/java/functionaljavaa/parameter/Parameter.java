/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author Administrator
 */
public class Parameter {
    public enum PropertyFilesType{TRANSLATION_DIR_PATH("translationDirPath", "="),
        PROCEDURE_BUSINESS_RULES_DIR_PATH("procedureBusinessRulesDirPath", ":")
        ;
        private PropertyFilesType(String appConfigParamName, String appConfigSeparator){
            this.appConfigParamName=appConfigParamName;
            this.appConfigSeparator=appConfigSeparator;
        }
        public String getAppConfigParamName(){            return this.appConfigParamName;        }        
        public String getAppConfigSeparator(){            return this.appConfigSeparator;        }        
        private final String appConfigParamName;
        private final String appConfigSeparator;
    }
    /**
     *
     */
    public static final String BUNDLE_TAG_PARAMETER_CONFIG_CONF="parameter.config.app-config";

    /**
     *
     */
    //public static final String BUNDLE_TAG_TRANSLATION_DIR_PATH="translationDirPath";
    //public static final String BUNDLE_TAG_PROCEDURE_BUSINESS_RULES_DIR_PATH="procedureBusinessRulesDirPath";
    

    /**
     *  Get the parameter value or blank otherwise.
     * @param parameterFolder - The directoy name LabPLANET (api messages/error trapping)/config (procedure business rules) (if null then config)
     * @param procName - procedureName
     * @param schemaSuffix - The procedure schema: config/data/procedure. 
     * @param parameterName - Tag name
     * @param language - Language
     * @return
     **/
    public static String getParameterBundle(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language) {
        String className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
        String classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
        String methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
        Integer lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           

        ResourceBundle prop = null;
        if (parameterFolder==null){parameterFolder="config";}
        String filePath = "parameter."+parameterFolder+"."+procName;
        if (schemaSuffix!=null){filePath=filePath+"-"+schemaSuffix;}
        if (language != null) {filePath=filePath+"_" + language;}
        
        try {
            prop = ResourceBundle.getBundle(filePath);
            if (!prop.containsKey(parameterName)) {              
                LPPlatform.saveParameterPropertyInDbErrorLog(procName, parameterFolder, 
                        new Object[]{className, classFullName, methodName, lineNumber}, parameterName);
                return "";
            } else {
                return prop.getString(parameterName);
            }
        } catch (Exception e) {
            LPPlatform.saveParameterPropertyInDbErrorLog(procName, parameterFolder, 
                    new Object[]{className, classFullName, methodName, lineNumber}, parameterName);
            return "";
        }
    }
    

    /**
     *  Check if a parameter is part or not of a properties file
     * @param parameterFolder - The directoy name LabPLANET (api messages/error trapping)/config (procedure business rules) (if null then config)
     * @param schemaName - procedureName
     * @param areaName - The procedure schema: config/data/procedure. 
     * @param parameterName - Tag name
     * @param language - Language
     * @return
     */
    public Boolean parameterInFile(String parameterFolder, String schemaName, String areaName, String parameterName, String language){
        return !"".equals(getParameterBundle(parameterFolder, schemaName, areaName, parameterName, language));
    }
    /**
     *
     * @param parameterName
     * @return
     */
    public static String getParameterBundleAppFile(String parameterName) {
        return getParameterBundleInAppFile("parameter.config.app", parameterName);
    }

    public static String getParameterBundleInConfigFile(String configFile, String parameterName, String language) {
        return getParameterBundleInAppFile("parameter.config." + configFile + "_" + language, parameterName);
    }

    private static String getParameterBundleInAppFile(String fileUrl, String parameterName) {
        /*String className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
        String classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
        String methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
        Integer lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
        */
        try {
            ResourceBundle prop = ResourceBundle.getBundle(fileUrl);
            if (!prop.containsKey(parameterName)) {
                LPPlatform.saveParameterPropertyInDbErrorLog("", fileUrl, 
                        new Object[]{}, parameterName);
                return "";
            } else {
                return prop.getString(parameterName);
            }
        } catch (Exception e) {
            LPPlatform.saveParameterPropertyInDbErrorLog("", fileUrl, 
                    new Object[]{}, parameterName);
            return e.getMessage();
        }
    }
    
    /**
     *
     * @param configFile
     * @param parameterName
     * @return
     */
    public static String getParameterBundle(String configFile, String parameterName) {
        /*StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = Thread.currentThread().toString();
                Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
        String classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
        String methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
        Integer lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
        */
        try {            
            ResourceBundle prop = ResourceBundle.getBundle("parameter.config." + configFile.replace("\"", ""));
            if (!prop.containsKey(parameterName)) {
                LPPlatform.saveParameterPropertyInDbErrorLog("", configFile, 
                        //new Object[]{className, classFullName, methodName, lineNumber}, 
                        new Object[]{}, 
                        parameterName);
                return "";
            } else {
                return prop.getString(parameterName);
            }
        } catch (Exception e) {
            LPPlatform.saveParameterPropertyInDbErrorLog("", configFile, 
                    new Object[]{}, parameterName);
            return "";
        }
    }
    

    /**
     * Not in use
     * @param type
     * @param fileName
     * @param entryName
     * @param entryValue
     * @return
     */
    public String addTagInPropertiesFile(String type, String fileName, String entryName, String entryValue){
        StringBuilder newEntryBuilder = new StringBuilder(0);
        String fileDir=getFileDirByPropertyFileType(type);
        if (fileDir.contains(LPPlatform.LAB_FALSE)) return fileDir;
        PropertyFilesType propFileType = PropertyFilesType.valueOf(type.toUpperCase());
        File[] transFiles = propertiesFiles(fileDir, fileName);
        for (File f: transFiles)
        {
            String fileidt = fileDir + "\\" + f.getName();
            try{    
                String paramExists=getParameterBundle(fileName, entryName);
                if (paramExists.length()>0 && paramExists.equalsIgnoreCase(entryValue) )
                    return "the parameter "+entryName+" already exists in properties file "+fileName+" . (Path:"+fileDir+")";
                String newLogEntry = " created tag in " + f.getName() + " for the entry " + entryName + " and value " + entryValue;
//                if (fileName.equalsIgnoreCase("USERNAV")){ newEntryBuilder.append(entryName).append(":").append(entryValue);}
//                else { newEntryBuilder.append(entryName).append("=").append(entryValue);}
                newEntryBuilder.append(entryName).append(propFileType.appConfigSeparator).append(entryValue);

                try (FileWriter fw = new FileWriter(fileidt, true)){
                    if (newEntryBuilder.length()>=0){
                        newEntryBuilder.append("\n");
                        fw.append(newEntryBuilder.toString());
                        }
                } catch (IOException ex1) {
                    Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex1);
                    return ex1.getMessage();
                }
                return newLogEntry;

//                return " Exists the tag in " + f.getName() + " for the entry " + entryName + " and value " + entryValue;
            }catch(MissingResourceException ex)
            {
                String newLogEntry = " created tag in " + f.getName() + " for the entry " + entryName + " and value " + entryValue;

                if (fileName.equalsIgnoreCase("USERNAV")){ newEntryBuilder.append(entryName).append(":").append(entryValue);}
                else { newEntryBuilder.append(entryName).append("=").append(entryValue);}
                
                try (FileWriter fw = new FileWriter(fileidt, true)){
                    if (newEntryBuilder.length()>=0){
                        newEntryBuilder.append("\n");
                        fw.append(newEntryBuilder.toString());
                        }
                } catch (IOException ex1) {
                    Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex1);
                    return ex1.getMessage();
                }
                return newLogEntry;
            }
        }    
        return "Nothing done";
    }
    
    public String createPropertiesFile(String type, String fileName){
        try {
            String fileDir=getFileDirByPropertyFileType(type);
            if (fileDir.contains(LPPlatform.LAB_FALSE)) return fileDir;
            File[] transFiles = propertiesFiles(fileDir, fileName);
            if (transFiles.length>0) return "file "+fileName+" already exists";
            File newFile = new File(fileDir+fileName+".properties");
            boolean createNewFile = newFile.createNewFile();
            if (createNewFile) return "New properties file created, "+fileName;
                
            return "Nothing done";
        } catch (IOException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
    }
            
    public String getFileDirByPropertyFileType(String type){
        PropertyFilesType endPoint = null;
        try{
            endPoint = PropertyFilesType.valueOf(type.toUpperCase());
        }catch(Exception e){
            return "argument type value is "+type+"and should be one of TRANSLATION, PROCEDURE_BUSINESS_RULE";
            //LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.API_ERRORTRAPING_PROPERTY_ENDPOINT_NOT_FOUND, new Object[]{actionName, this.getServletName()}, language);              
            //return;                   
        }

        String fileDir = "";
        ResourceBundle propConfig = ResourceBundle.getBundle(BUNDLE_TAG_PARAMETER_CONFIG_CONF);  
        switch(endPoint){
            case TRANSLATION_DIR_PATH:
                fileDir = propConfig.getString(PropertyFilesType.TRANSLATION_DIR_PATH.getAppConfigParamName());
                break;
            case PROCEDURE_BUSINESS_RULES_DIR_PATH:
                fileDir = propConfig.getString(PropertyFilesType.PROCEDURE_BUSINESS_RULES_DIR_PATH.getAppConfigParamName());
                break;
            default:
                return LPPlatform.LAB_FALSE+"argument type value is "+type+"and should be one of TRANSLATION, PROCEDURE_BUSINESS_RULE";
        }
        fileDir = fileDir.replace("/", "\\");        
        return fileDir;
    }    
    /**
     *
     * @param fileName
     * @return
     */
    public File[] propertiesFiles(String fileName){

        ResourceBundle propConfig = ResourceBundle.getBundle(BUNDLE_TAG_PARAMETER_CONFIG_CONF);        
        String translationsDir = propConfig.getString(PropertyFilesType.TRANSLATION_DIR_PATH.getAppConfigParamName());
        translationsDir = translationsDir.replace("/", "\\");

        File dir = new File(translationsDir);
        return dir.listFiles((File dir1, String name) -> name.contains(fileName));       
    }    
    
    public File[] propertiesFiles(String propFilesDir, String fileName){
        //ResourceBundle propConfig = ResourceBundle.getBundle(BUNDLE_TAG_PARAMETER_CONFIG_CONF);        
        //String translationsDir = propConfig.getString(BUNDLE_TAG_TRANSLATION_DIR_PATH);
        //translationsDir = translationsDir.replace("/", "\\");

        File dir = new File(propFilesDir);
        return dir.listFiles((File dir1, String name) -> name.contains(fileName));       
    }     
    private static final int CLIENT_CODE_STACK_INDEX;    
    static{
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()){
            i++;
            if (ste.getClassName().equals(LPPlatform.class.getName())){
                break;
            }
        }
        CLIENT_CODE_STACK_INDEX = i;
    }   
     
}
