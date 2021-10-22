/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.parameter;

import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import functionaljavaa.testingscripts.TestingMessageCodeVisited;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author Administrator
 */
public class Parameter {
    public enum PropertyFilesType{TRANSLATION_DIR_PATH("translationDirPath", "="),
        PROCEDURE_BUSINESS_RULES_DIR_PATH("procedureBusinessRulesDirPath", ":"),
        ENDPOINTDOCUMENTATION("EndPointDocumentation", ":"),
        AUDITEVENTS("AUDITEVENTS", ":")
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
     *  Get the parameter value or blank otherwise.
     * @param parameterFolder - The directoy name LabPLANET (api messages/error trapping)/config (procedure business rules) (if null then config)
     * @param procName - procedureName
     * @param schemaSuffix - The procedure schema: config/data/procedure. 
     * @param parameterName - Tag name
     * @param language - Language
     * @return
     **/
    public static String getMessageCodeValue(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language) {
        return getMessageCodeValue(parameterFolder, procName, schemaSuffix, parameterName, language, true);       
    }
    public static String getMessageCodeValue(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language, Object[] callerInfo, Boolean reportMissingProp) {
        if (reportMissingProp==null) reportMissingProp=true;
        return getMessageCodeValue(parameterFolder, procName, schemaSuffix, parameterName, language, reportMissingProp, null, callerInfo);       
    }
    public static String parameterBundleExists(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language, Boolean reportMissingProp) {
        return getMessageCodeValue(parameterFolder, procName, schemaSuffix, parameterName, language, reportMissingProp, true, null);        
    }
    public static String getMessageCodeValue(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language, Boolean reportMissingProp) {    
        return getMessageCodeValue(parameterFolder, procName, schemaSuffix, parameterName, language, reportMissingProp, null, null);
    }
    private static String getMessageCodeValue(String parameterFolder, String procName, String schemaSuffix, String parameterName, String language, Boolean reportMissingProp, Boolean returnFalseIfMissing, Object[] callerInfo) {
        ResourceBundle prop = null;
        if (parameterFolder==null){parameterFolder="config";}
        String filePath = "parameter."+parameterFolder+"."+procName;
        if (schemaSuffix!=null){filePath=filePath+"-"+LPNulls.replaceNull(schemaSuffix);}
        if (language != null) {filePath=filePath+"_" +LPNulls.replaceNull(language);}        
        else{ 
            if (filePath.toLowerCase().contains("parameter.labplanet"))
                filePath=filePath+"_" + GlobalVariables.Languages.EN.getName();
        }
        try {
            prop = ResourceBundle.getBundle(filePath);
            if ((!prop.containsKey(parameterName)) && reportMissingProp!=null && reportMissingProp) {  
                if (parameterName.toLowerCase().contains("encrypted_")) return ""; 
                LPPlatform.saveParameterPropertyInDbErrorLog(procName, parameterFolder, 
                        //new Object[]{className, classFullName, methodName, lineNumber}, 
                        callerInfo,
                        parameterName);
                TestingMessageCodeVisited testingMessageCodeVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingMessageCodeVisitedObj();
                if (testingMessageCodeVisitedObj!=null)
                    testingMessageCodeVisitedObj.AddObject(procName, schemaSuffix, parameterName, "Not found!");
                return "";
            } else {
                String parameterValue = prop.getString(parameterName);
                TestingMessageCodeVisited testingMessageCodeVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingMessageCodeVisitedObj();
                if (testingMessageCodeVisitedObj!=null)
                    testingMessageCodeVisitedObj.AddObject(procName, schemaSuffix, parameterName, parameterValue);
                return parameterValue;
            }
        } catch (Exception e) {            
            if (returnFalseIfMissing!=null && returnFalseIfMissing) 
                return LPPlatform.LAB_FALSE;
            if (reportMissingProp!=null && !reportMissingProp) return "";
            if (parameterName.toLowerCase().contains("encrypted_")) return "";            
            LPPlatform.saveParameterPropertyInDbErrorLog(procName, parameterFolder, 
                    //new Object[]{className, classFullName, methodName, lineNumber}, 
                    callerInfo,
                    parameterName);
            TestingMessageCodeVisited testingMessageCodeVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingMessageCodeVisitedObj();
            if (testingMessageCodeVisitedObj!=null)
                testingMessageCodeVisitedObj.AddObject(procName, schemaSuffix, parameterName, "ERROR: Not Found!");            
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
        return !"".equals(getMessageCodeValue(parameterFolder, schemaName, areaName, parameterName, language));
    }

    /**
     *
     * @param parameterName
     * @return
     */
    public static String getBusinessRuleAppFile(String parameterName) {
        String className ="NO_TRACE";
        String classFullName = "NO_TRACE";
        String methodName = "NO TRACE"; 
        Integer lineNumber = -999999;
        if (Thread.currentThread().getStackTrace().length>CLIENT_CODE_STACK_INDEX){        
            className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber(); 
        }
        className = className.replace(".java", "");
        Object[] callerInfo=new Object[]{className, classFullName, methodName, lineNumber};
        return getBusinessRuleInAppFile("parameter.config.app", parameterName, callerInfo);
    }

    public static String getBusinessRuleInConfigFile(String configFile, String parameterName, String language) {
        String className ="NO_TRACE";
        String classFullName = "NO_TRACE";
        String methodName = "NO TRACE"; 
        Integer lineNumber = -999999;
        if (Thread.currentThread().getStackTrace().length>CLIENT_CODE_STACK_INDEX){
            className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber(); 
        }
        className = className.replace(".java", "");
        Object[] callerInfo=new Object[]{className, classFullName, methodName, lineNumber};
        return getBusinessRuleInAppFile("parameter.config." + configFile + "_" + language, parameterName, callerInfo);
    }

    private static String returnBusinessRuleValue(String valueToReturn, String procInstanceName, String area, String parameterName, Object[] callerInfo){        
        if (valueToReturn==null || valueToReturn.length()==0)
            LPPlatform.saveParameterPropertyInDbErrorLog("", procInstanceName+"-"+area, 
                callerInfo, parameterName);
        if (ProcedureRequestSession.getInstanceForActions(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, area, callerInfo[0].toString(), parameterName, valueToReturn);        
        }
        return valueToReturn;
    }
    public static String getBusinessRuleProcedureFile(String procInstanceName, String suffixFile, String parameterName) {
        String className ="NO_TRACE";
        String classFullName = "NO_TRACE";
        String methodName = "NO TRACE"; 
        Integer lineNumber = -999999;
        if (Thread.currentThread().getStackTrace().length>CLIENT_CODE_STACK_INDEX){
            className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
            classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
            methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
            lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber(); 
        className = className.replace(".java", "");
        }
        Object[] callerInfo=new Object[]{className, classFullName, methodName, lineNumber};

        BusinessRules brTesting=ProcedureRequestSession.getInstanceForActions(null, null, null).getBusinessRulesTesting();
        BusinessRules brProcInstance=ProcedureRequestSession.getInstanceForActions(null, null, null).getBusinessRulesProcInstance();
        //BusinessRules br=new BusinessRules(procInstanceName);
        if (brTesting!=null){
            String brValue=brTesting.getProcedureBusinessRule(parameterName);
            if (brValue.length()>0) return returnBusinessRuleValue(brValue, procInstanceName, suffixFile, parameterName, callerInfo);
        }
        if (brProcInstance!=null){
            String brValue=brProcInstance.getConfigBusinessRule(parameterName);
            if (brValue.length()>0) return returnBusinessRuleValue(brValue, procInstanceName, suffixFile, parameterName, callerInfo);
            brValue=brProcInstance.getDataBusinessRule(parameterName);
            if (brValue.length()>0) return returnBusinessRuleValue(brValue, procInstanceName, suffixFile, parameterName, callerInfo);
            brValue=brProcInstance.getProcedureBusinessRule(parameterName);
            if (brValue.length()>0) return returnBusinessRuleValue(brValue, procInstanceName, suffixFile, parameterName, callerInfo);
        }
        return returnBusinessRuleValue("", procInstanceName, suffixFile, parameterName, callerInfo);
    }
    
    private static String getBusinessRuleInAppFile(String fileUrl, String parameterName, Object[] callerInfo) {
        /*String className = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getFileName(); 
        String classFullName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getClassName(); 
        String methodName = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getMethodName(); 
        Integer lineNumber = Thread.currentThread().getStackTrace()[CLIENT_CODE_STACK_INDEX].getLineNumber();           
        */
        try {
            ResourceBundle prop = ResourceBundle.getBundle(fileUrl);
            if (!prop.containsKey(parameterName)) {
                if (parameterName.toLowerCase().contains("encrypted_")) return ""; 
                LPPlatform.saveParameterPropertyInDbErrorLog("", fileUrl, 
                        new Object[]{}, parameterName);
                return "";
            } else {
                return prop.getString(parameterName);
            }
        } catch (Exception e) {
            if (parameterName.toLowerCase().contains("encrypted_")) return "";
            LPPlatform.saveParameterPropertyInDbErrorLog("", fileUrl, 
                    callerInfo, parameterName);
            return e.getMessage();
        }
    }
    
    /**
     *
     * @param configFile
     * @param parameterName
     * @return
     */
    public static String getMessageCodeValue(String configFile, String parameterName) {
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
                if (parameterName.toLowerCase().contains("encrypted_")) return ""; 
                LPPlatform.saveParameterPropertyInDbErrorLog("", configFile, 
                        //new Object[]{className, classFullName, methodName, lineNumber}, 
                        new Object[]{}, 
                        parameterName);
                return "";
            } else {
                return prop.getString(parameterName);
            }
        } catch (Exception e) {
            if (parameterName.toLowerCase().contains("encrypted_")) return "";
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
                String paramExists=getMessageCodeValue(fileName, entryName);
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
            if (transFiles!=null && transFiles.length>0) return "file "+fileName+" already exists";
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
            try{
            endPoint = PropertyFilesType.valueOf(type);
            }catch(Exception e2){
                return "argument type value is "+type+"and should be one of TRANSLATION, PROCEDURE_BUSINESS_RULE, ENDPOINT_DOC_FOLDER";
            }
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
            case ENDPOINTDOCUMENTATION:
                fileDir = propConfig.getString(PropertyFilesType.ENDPOINTDOCUMENTATION.getAppConfigParamName());
                break;
            default:
                return LPPlatform.LAB_FALSE+"argument type value is "+type+"and should be one of TRANSLATION, PROCEDURE_BUSINESS_RULE, ENDPOINT_DOC_FOLDER";
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
    
    public static Boolean isTagValueOneOfEnableOnes(String tagValue){
        String enableValuesStr=getBusinessRuleAppFile("businessRulesEnableValues"); 
        String[] enableValues=enableValuesStr.split("\\|");
        return LPArray.valueInArray(enableValues, tagValue);
    }
     
}
