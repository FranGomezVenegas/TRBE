/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

import databases.SqlStatement;
import functionaljavaa.parameter.Parameter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import databases.Rdbms;
import databases.TblsCnfg;
import databases.Token;
import functionaljavaa.businessrules.BusinessRules;
import static functionaljavaa.parameter.Parameter.getBusinessRuleAppFile;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;

/**
 * LPPlatform is a library for methods solving topics that are specifically part of the LabPLANET Paradigm.
 * @author Fran Gomez
 */
public class LPPlatform {
    String classVersion = "0.1";
    public static final String AUDIT_FIELDS_UPDATED_SEPARATOR=":";
    /**
     *
     */
    public static final String LAB_ENCODER_UTF8 = "utf-8";

    /**
     *
     */
    public static final String LAB_TRUE = "LABPLANET_TRUE";

    /**
     *
     */
    public static final String LAB_FALSE = "LABPLANET_FALSE";
    
    /**
     *
     */
    public static final String CONFIG_FILES_FOLDER = "LabPLANET";
    
    public static final String CONFIG_FILES_ERRORTRAPING = "errorTraping";

    /**
     *
     */
    public static final String CONFIG_FILES_API_ERRORTRAPING = "api-platform";
    public static final String CONFIG_FILES_API_SUCCESSMESSAGE = "apiSuccessMsg_";
    public static final String CONFIG_FILES_LOCKING_REASONS = "lockingReasons";
    public static final String CONFIG_FILES_WARNING_REASONS = "warningReasons";
    
    

    public enum ApiErrorTraping  implements EnumIntMessages{
        EXCEPTION_RAISED("exceptionRaised", "", ""),
        PROPERTY_DATABASE_NOT_CONNECTED("databaseConnectivityError", "", ""),
        MANDATORY_PARAMS_MISSING("MissingMandatoryParametersInRequest", "", ""),
        PROPERTY_ENDPOINT_NOT_FOUND("endPointNotFound", "", ""),
        INVALID_TOKEN("invalidToken", "", ""),
        INVALID_USER_VERIFICATION("invalidUserVerification", "", ""),
        INVALID_ESIGN("invalidEsign", "", ""),
        REGRESSIONTESTING_ACTIONSNOTALLOWEDFORPROC("regressionTesting_actionsNotAllowedOrDeclaredAsPartOfThisProcedure", "", ""),
        PROPERTY_NOT_CREATED("propertyNotCreated", "property created <*1*>", "propiedad creada <*1*>"),
        ;
        private ApiErrorTraping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }
   public enum LpPlatformBusinessRules implements EnumIntBusinessRules {
        PROCEDURE_ACTIONS("procedureActions", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        ACTION_ENABLED_ROLES("actionEnabled", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        ESIGN_REQUIRED("eSignRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        VERIFYUSER_REQUIRED("verifyUserRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        ACTIONCONFIRM_REQUIRED("actionConfirmRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        AUDIT_JUSTIF_REASON_REQUIRED("auditJustifReasonRequired", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        AUDITREASON_PHRASE("AuditReasonPhrase", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', true, null),
        TABLE_MANDATORYFIELDS_ACTIONNAME("_mandatoryFields", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', false, null),
        SUFFIX_CONFIGTABLENAME("_configTableName", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', true, null),
        SUFFIX_CONFIGTABLEKEYFIELDS("_configTableKeyFields", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', true, null),
        SUFFIX_SPECIALFIELDNAME("_specialFieldsCheck", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', true, null),
        SUFFIX_SPECIALFIELDMETHODNAME("_specialFieldsCheck_methodName", GlobalVariables.Schemas.CONFIG.getName(), null, null, '|', true, null),
        PREFIX_ENCRYPTED_TABLENAME("encrypted_", "", null, null, '|', true, null),
        MIDDLEOF_FIELDSADDINGMANDATORY("_fieldsAddingMandatory", "", null, null, '|', true, null),
        MARK_EXPIRED_OBJECTS("markExpiredObjects", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', true, null),
        MARK_EXPIRED_OBJECTS_LAST_RUN("markExpiredObjectsLastRun", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', true, null),
        ;
        private LpPlatformBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator, Boolean opt, ArrayList<String[]> getPreReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=opt;
            this.getPreReqs=getPreReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional(){return this.isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.getPreReqs;}        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;  
        private final Boolean isOptional;
        private final ArrayList<String[]> getPreReqs;
   }
public enum LpPlatformSuccess implements EnumIntMessages{  
        USRROLACTIONENABLED_ENABLED("userRoleActionEnabled_enabled", "", ""),
        USRROLACTIONENABLED_ENABLED_BYALL("userRoleActionEnabled_ALL", "", ""),
        VERIFYUSERREQUIRED_ENABLED_BY_ALL("verifyUserRequired_ALL", "", ""),
        VERIFYUSERREQUIRED_ENABLED("verifyUserRequired_enabled", "", ""),
        ESIGNREQUIRED_ENABLED_BY_ALL("esignRequired_ALL", "", ""),
        ESIGNREQUIRED_ENABLED("esignRequired_enabled", "", ""),
        JUSTIFPHRASEREQUIRED_ENABLED_BY_ALL("justificationPhraseRequired_ALL", "", ""),
        JUSTIFPHRASEREQUIRED_ENABLED("justificationPhraseRequired_enabled", "", ""),
        SPECIALFUNCTION_ALLSUCCESS("SpecialFunctionAllSuccess", "", ""),
        PROPERTY_CREATED("propertyCreated", "property created <*1*>", "propiedad creada <*1*>"),
        COMMA_IS_DECIMAL_SEPARATOR("commaIsTheDecimalsSeparator", "", ""),
        CORRECT("correct", "", ""),
        ALL_FINE("allFine", "", ""),
        ALL_THE_SAME("allTheSame", "", ""),
        USER_CERTIFICATION_IS_ENABLED ("isUserCertificationEnabled_yes", "", ""),
        AUDIT_AND_USER_VALIDATION_CHECK_SUCCESS ("checkUserValidationPassesSuccess", "", ""),        
        ; 
        private LpPlatformSuccess(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        @Override        public String getErrorCode(){return this.errorCode;}
        @Override        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        @Override        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

public enum LpPlatformErrorTrapping implements EnumIntMessages{  
        RULE_NAME_VALUE("LpPlatform_ruleNameValue", "Rule name = <*1*>", "Nombre de la regla = <*1*>"),
        BUS_RUL_REVIEWBYTESTINGGROUP_NOT_FOUND("LpPlatform_BusinessRulesampleTestingByGroup_ReviewByTestingGroupNotFound", "sampleTestingByGroup_ReviewByTestingGroup not found or not define", "Regla de negocio sampleTestingByGroup_ReviewByTestingGroup no encontrada o no definida"),
        USER_NOTASSIGNED_TOPROCEDURE("userNotAssignedToProcedure", "", ""),
        USRROLACTIONENABLED_DENIED_RULESNOTFOUND("userRoleActionEnabled_denied_rulesNotFound", "", ""),
        USRROLACTIONENABLED_DENIED("userRoleActionEnabled_denied", "", ""),
        USRROLACTIONENABLED_ACTIONENABLEDFORROLES_BUSRULE_NOTFOUND("userRoleActionEnabled_actionEnabledForRolesBusRuleNotFound", "", ""),
        USRROLACTIONENABLED_MISSEDPARAMETER("userRoleActionEnabled_missedParameter", "", ""),
        USRROLACTIONENABLED_ROLENOTINCLUDED("userRoleActionEnabled_roleNotIncluded", "", ""),
        VERIFYUSERREQUIRED_ENABLED_BY_ALL("verifyUserRequired_ALL", "", ""),
        VERIFYUSERREQUIRED_DENIED_RULENOTFOUND("verifyUserRequired_denied_ruleNotFound", "", ""),
        VERIFYUSERREQUIRED_DENIED("verifyUserRequired_denied", "", ""),
        ESIGNREQUIRED_DENIED_RULENOTFOUND("esign_denied_ruleNotFound", "", ""),
        ESIGNREQUIRED_DENIED("esignRequired_denied", "", ""),
        JUSTIFPHRASEREQUIRED_DENIED_RULENOTFOUND("justificationPhraseRequired_denied_ruleNotFound", "", ""),
        JUSTIFPHRASEREQUIRED_DENIED("justificationPhraseRequired_denied", "", ""),
        MISSINGTABLECONFIGCODE("LabPLANETPlatform_MissingTableConfigCode", "", ""),
        SPECIALFUNCTION_RETURNEDERROR("LabPLANETPlatform_SpecialFunctionReturnedERROR", "", ""),
        SPECIALFUNCTION_CAUSEDEXCEPTION("LabPLANETPlatform_SpecialFunctionCausedException", "", ""),
        MIRROR_MISMATCHES("ProcedureDeployment_mirrorMismatches", "", ""),
        NEWDATETIMENULL_OR_WRONGFORMAT("newDateTimeNullOrWrongFormat","",""),
        ; 
        private LpPlatformErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
            this.errorCode=errCode;
            this.defaultTextWhenNotInPropertiesFileEn=defaultTextEn;
            this.defaultTextWhenNotInPropertiesFileEs=defaultTextEs;
        }
        public String getErrorCode(){return this.errorCode;}
        public String getDefaultTextEn(){return this.defaultTextWhenNotInPropertiesFileEn;}
        public String getDefaultTextEs(){return this.defaultTextWhenNotInPropertiesFileEs;}
    
        private final String errorCode;
        private final String defaultTextWhenNotInPropertiesFileEn;
        private final String defaultTextWhenNotInPropertiesFileEs;
    }

    public static final String REQUEST_PARAM_FILE_PATH = "filePath";
    public static final String REQUEST_PARAM_FILE_NAME = "fileName";
    public static final String REQUEST_PARAM_LANGUAGE = "language";

    public static final String CONFIG_PROC_CONFIG_FILE_NAME = "config";
    public static final String CONFIG_PROC_DATA_FILE_NAME = "data";
    public static final String CONFIG_PROC_FILE_NAME = "procedure";
    private static final String ENCRYPTION_KEY = "Bar12345Bar12345";

    /**
     *
     */
    public static final String BUSINESS_RULES_VALUE_ENABLED="ENABLE";    
    public static final Object[] breakPointArray=new Object[]{"MissingMandatoryParametersInRequest"};

    /**
     *
     * @param procInstanceName
     * @param token
     * @param actionName
     * @return
     */
    public static Object[] procActionEnabled(String procInstanceName, Token token, String actionName, BusinessRules procBusinessRules){
        
        String userProceduresList=token.getUserProcedures();
        userProceduresList=userProceduresList.replace("[", "");
        userProceduresList=userProceduresList.replace("]", "");        
        if (!LPArray.valueInArray(userProceduresList.split(", "), procInstanceName))
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.USER_NOTASSIGNED_TOPROCEDURE, new String[]{token.getUserName(), procInstanceName, userProceduresList});
        
        actionName = actionName.toUpperCase();        
        String[] procedureActions = procBusinessRules.getProcedureBusinessRule(LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName()).split("\\|");
        if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "TestingRegresssionUAT", LpPlatformBusinessRules.PROCEDURE_ACTIONS.getTagName(), Arrays.toString(procedureActions));        
        }
        
        if (LPArray.valueInArray(procedureActions, "ALL")){
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.USRROLACTIONENABLED_ENABLED_BYALL, new String[]{procInstanceName, actionName});
        }
        if ( (procedureActions.length==1 && "".equals(procedureActions[0])) ){
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.USRROLACTIONENABLED_DENIED_RULESNOTFOUND, new String[]{procInstanceName, Arrays.toString(procedureActions)});
        }else if(!LPArray.valueInArray(procedureActions, actionName)){    
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.USRROLACTIONENABLED_DENIED, new String[]{actionName, procInstanceName, Arrays.toString(procedureActions)});            
        }else{
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new String[]{procInstanceName, actionName});               
        }    
    }    
    /**
     *
     * @param procInstanceName
     * @param userRole
     * @param actionName
     * @return
     */
    public static Object[] procUserRoleActionEnabled(String procInstanceName, String userRole, String actionName, BusinessRules procBusinessRules){
        String[] procedureActionsUserRoles = procBusinessRules.getProcedureBusinessRule(LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+actionName).split("\\|"); //Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getAreaName(), LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+actionName).toString().split("\\|");
        if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "TestingRegresssionUAT", LpPlatformBusinessRules.ACTION_ENABLED_ROLES.getTagName()+actionName, Arrays.toString(procedureActionsUserRoles));        
        }

        //Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "actionEnabled"+actionName).split("\\|");
        
        if (LPArray.valueInArray(procedureActionsUserRoles, "ALL")){
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.USRROLACTIONENABLED_ENABLED_BYALL, new Object[]{procInstanceName});
        }
        if ( (procedureActionsUserRoles.length==1 && "".equals(procedureActionsUserRoles[0])) ){
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.USRROLACTIONENABLED_MISSEDPARAMETER, new Object[]{procInstanceName, actionName});        
        }else if(!LPArray.valueInArray(procedureActionsUserRoles, userRole)){    
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.USRROLACTIONENABLED_ROLENOTINCLUDED, new Object[]{procInstanceName, actionName, userRole, Arrays.toString(procedureActionsUserRoles)});        
        }else{
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.USRROLACTIONENABLED_ENABLED, new Object[]{procInstanceName, actionName});        
        }            
    }
    
    /**    
     *
     * @param procInstanceName
     * @param actionName
     * @return
     */ 
    public static Object[] procActionRequiresUserConfirmation(String procInstanceName, String actionName, BusinessRules procBusinessRules){        
        actionName = actionName.toUpperCase();
        String[] actionRequiresUserConfirmationRuleValue = procBusinessRules.getProcedureBusinessRule(LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName()).split("\\|"); // Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getAreaName(), LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName()).toString().split("\\|");
        if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "TestingRegresssionUAT", LpPlatformBusinessRules.VERIFYUSER_REQUIRED.getTagName()+actionName, Arrays.toString(actionRequiresUserConfirmationRuleValue));        
        }

        //Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "verifyUserRequired").split("\\|");        
        if (LPArray.valueInArray(actionRequiresUserConfirmationRuleValue, "ALL")){
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformErrorTrapping.VERIFYUSERREQUIRED_ENABLED_BY_ALL, new Object[]{procInstanceName, actionName});
        }
        if ( (actionRequiresUserConfirmationRuleValue.length==1 && "".equals(actionRequiresUserConfirmationRuleValue[0])) ){
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.VERIFYUSERREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, Arrays.toString(actionRequiresUserConfirmationRuleValue)});
        }else if(!LPArray.valueInArray(actionRequiresUserConfirmationRuleValue, actionName)){    
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.VERIFYUSERREQUIRED_DENIED, new Object[]{actionName, procInstanceName, Arrays.toString(actionRequiresUserConfirmationRuleValue)});
        }else{
            String diagnStr = LAB_TRUE;
            diagnStr=diagnStr+LPNulls.replaceNull(auditReasonType(procInstanceName, actionName));
            return ApiMessageReturn.trapMessage(diagnStr, LpPlatformSuccess.VERIFYUSERREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});
        }    
    }    

    /** VERIFYUSERREQUIRED_ENABLED_BY_ALL VERIFYUSERREQUIRED_DENIED_RULENOTFOUND VERIFYUSERREQUIRED_DENIED VERIFYUSERREQUIRED_ENABLED
     *
     * @param procInstanceName
     * @param actionName
     * @return
     */
    public static Object[] procActionRequiresEsignConfirmation(String procInstanceName, String actionName, BusinessRules procBusinessRules){
        actionName = actionName.toUpperCase();
        String[] procedureActions = procBusinessRules.getProcedureBusinessRule(LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()).split("\\|"); // Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName(), LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()).toString().split("\\|");
        if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "TestingRegresssionUAT", LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()+actionName, Arrays.toString(procedureActions));        
        }
                //Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "eSignRequired").split("\\|");
        
        if (LPArray.valueInArray(procedureActions, "ALL"))
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.ESIGNREQUIRED_ENABLED_BY_ALL, new Object[]{procInstanceName, actionName});
        if ( (procedureActions.length==1 && "".equals(procedureActions[0])) ){
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.ESIGNREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, Arrays.toString(procedureActions)});
        }else if(!LPArray.valueInArray(procedureActions, actionName)){    
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.ESIGNREQUIRED_DENIED, new Object[]{actionName, procInstanceName, Arrays.toString(procedureActions)});
        }else{
            return ApiMessageReturn.trapMessage(LAB_TRUE+auditReasonType(procInstanceName, actionName), LpPlatformSuccess.ESIGNREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});               
        }    
    }    
    public static Object[] procActionRequiresJustificationPhrase(String procInstanceName, String actionName, BusinessRules procBusinessRules){
        actionName = actionName.toUpperCase();
        String[] procedureActions = procBusinessRules.getProcedureBusinessRule(LpPlatformBusinessRules.AUDIT_JUSTIF_REASON_REQUIRED.getTagName()).split("\\|"); // Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.ESIGN_REQUIRED.getAreaName(), LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()).toString().split("\\|");
        if (ProcedureRequestSession.getInstanceForQueries(null, null, null).getIsForTesting()){
            TestingBusinessRulesVisited testingBusinessRulesVisitedObj = ProcedureRequestSession.getInstanceForActions(null, null, null).getTestingBusinessRulesVisitedObj();
            if (testingBusinessRulesVisitedObj!=null)
                testingBusinessRulesVisitedObj.AddObject(procInstanceName, "procedure", "TestingRegresssionUAT", LpPlatformBusinessRules.ESIGN_REQUIRED.getTagName()+actionName, Arrays.toString(procedureActions));        
        }
                //Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, "eSignRequired").split("\\|");
        
        if (LPArray.valueInArray(procedureActions, "ALL"))
            return ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.JUSTIFPHRASEREQUIRED_ENABLED_BY_ALL, new Object[]{procInstanceName, actionName});
        if ( (procedureActions.length==1 && "".equals(procedureActions[0])) ){
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.JUSTIFPHRASEREQUIRED_DENIED_RULENOTFOUND, new Object[]{procInstanceName, Arrays.toString(procedureActions)});
        }else if(!LPArray.valueInArray(procedureActions, actionName)){    
            return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.JUSTIFPHRASEREQUIRED_DENIED, new Object[]{actionName, procInstanceName, Arrays.toString(procedureActions)});
        }else{
            return ApiMessageReturn.trapMessage(LAB_TRUE+auditReasonType(procInstanceName, actionName), LpPlatformSuccess.JUSTIFPHRASEREQUIRED_ENABLED, new Object[]{procInstanceName, actionName});               
        }    
    }    
    
    private static String auditReasonType(String procInstanceName, String actionName){
        String auditReasonType = LPNulls.replaceNull(Parameter.getBusinessRuleProcedureFile(procInstanceName, 
            LpPlatformBusinessRules.AUDITREASON_PHRASE.getAreaName(), 
            actionName+LpPlatformBusinessRules.AUDITREASON_PHRASE.getTagName())).toString();
                //Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, actionName+"AuditReasonPhrase");        
        if (auditReasonType.length()==0)return "TEXT";
        if (auditReasonType.length()>0 && auditReasonType.equalsIgnoreCase("DISABLE"))return "";
        if (auditReasonType.length()>0 && auditReasonType.equalsIgnoreCase("NO"))return "";
        return auditReasonType;
    }
    /**
     *
     * @param schemaName
     * @param tableName
     * @param fieldName
     * @return
     */
    public static Boolean isEncryptedField(String schemaName, String areaName, String tableName, String fieldName){
        Boolean diagnoses = false;
        if ((schemaName==null) || (tableName==null) || (fieldName==null) ) {return diagnoses;}
        String parameterName = LpPlatformBusinessRules.PREFIX_ENCRYPTED_TABLENAME.getTagName()+tableName;
        schemaName = schemaName.replace("\"", "");
        if ( fieldName.contains(" ")){fieldName=fieldName.substring(0, fieldName.indexOf(' '));}
        String tableEncrytedFields = Parameter.getBusinessRuleProcedureFile(schemaName, areaName, parameterName, true);
        if ( (tableEncrytedFields==null) ){return diagnoses;}
        if ( ("".equals(tableEncrytedFields)) ){return diagnoses;}        
        return LPArray.valueInArray(tableEncrytedFields.split("\\|"), fieldName);        
    }
        
    /**
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static HashMap<String, String> encryptEncryptableFieldsAddBoth(String fieldName, String fieldValue){    
        return encryptEncryptableFields(false, fieldName, fieldValue);
    }    

    /**
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static HashMap<String, String> encryptEncryptableFieldsOverride(String fieldName, String fieldValue){    
        return encryptEncryptableFields(true, fieldName, fieldValue);
    }    
    
    private static HashMap<String, String> encryptEncryptableFields(Boolean override, String fieldName, String fieldValue){        
        HashMap<String, String> hm = new HashMap<>();        
        StringBuilder newFieldValueBuilder = new StringBuilder(0);
        if (!fieldName.toUpperCase().contains("IN")){
            Object[] encStr = encryptString(fieldValue);
            if (override){
                newFieldValueBuilder.append(encStr[1].toString());
            }else{
                fieldName=fieldName+" in|";       
                newFieldValueBuilder.append(fieldValue).append("|").append(encStr[1]);
            }
        }else{
            SqlStatement sql = new SqlStatement();
            String separator = sql.inNotInSeparator(fieldName);
            String[] valuesArr = fieldValue.split(separator);
            String valuesEncripted = "";
            for (String fn: valuesArr){
                Object[] encStr = encryptString(fn);
                if (override){
                    valuesEncripted = encStr[1]+separator;
                }else{
                  if (newFieldValueBuilder.length()>0) newFieldValueBuilder.append(separator);
                    newFieldValueBuilder.append(fn);
                    newFieldValueBuilder.append(separator).append(encStr[1]);
                }                
            }
            if (valuesEncripted.length()>0){
                valuesEncripted=valuesEncripted.substring(0, valuesEncripted.length()-2);
                newFieldValueBuilder = new StringBuilder(0);
                newFieldValueBuilder.append(valuesEncripted);
            }                    
        }
        
        
        hm.put(fieldName, newFieldValueBuilder.toString());
        return hm;
    }
    
    /**
     *
     * @param stringToEncrypt
     * @return
     */
    public static Object[] encryptString(String stringToEncrypt){
        Object[] diagnoses = new Object[3];            

        String key = ENCRYPTION_KEY; // 128 bit key
        try{
            String text = stringToEncrypt;
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(text.getBytes());

            StringBuilder sb = new StringBuilder(0);
            for (byte b: encrypted) {
                sb.append((char)b);
            }

            // the encrypted String
            String enc = sb.toString();
            diagnoses[0] = true;
            diagnoses[1] = enc;            
            diagnoses[2] = stringToEncrypt; 
            return diagnoses;
        }
        catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){
            diagnoses[0] = false;
            diagnoses[1] = e.getMessage();            
            diagnoses[2] = stringToEncrypt; 
            return diagnoses;
        }             
    }  
    
    /**
     *
     * @param encryptedString
     * @return
     */
    public static Object[] decryptString(String encryptedString){
        Object[] diagnoses = new Object[3];
        String key = ENCRYPTION_KEY; 
        try{                    
            String enc = encryptedString;
            // Create key and cipher
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");

            // for decryption
            byte[] bb = new byte[enc.length()];
            for (int i=0; i<enc.length(); i++) {
                bb[i] = (byte) enc.charAt(i);
            }

            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decrypted = new String(cipher.doFinal(bb));
            diagnoses[0] = true;
            diagnoses[1] = decrypted;            
            diagnoses[2] = encryptedString; 
            return diagnoses;
        }
        catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e){            diagnoses[0] = false;
            diagnoses[1] = e.getMessage();            
            diagnoses[2] = encryptedString; 
            return diagnoses;
        }
    }    
      
/**
 * addJavaClassDoc is the method that should reduce the lines of code for justifying lines of code against its requirement
 * to keep the track about which is the requirement covered by each section in each method.
 * When running the code in Dev-Mode then it should mark as 'covered' the existing requirement or create one record for this given requirement
 * The parameter.config.testing-html-settings mandatoryFields_requerimentsJavaDoc defines which are the mandatory fields that should be added
 * to the peer fields/values to let this call be consider fill enough to proceed.
 * 
 * @param fields String[] - which are the properties being passed.
 * @param values Object[] - which are the values for the properties defined above
 * @param elementsDev StackTraceElement[] - Provides info from the context such as the ClassName + MethodName + LineNumber
 */
    public static void addJavaClassDoc(String[] fields, Object[] values, StackTraceElement[] elementsDev) {
                
        String schemaName = GlobalVariables.Schemas.REQUIREMENTS.getName();
        String tableName = "java_class_doc";
        String[] fldName = new String[0];
        Object[] fldValue = new Object[0];
        String currField = "";        

        fldName = LPArray.addValueToArray1D(fldName, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);         fldValue = LPArray.addValueToArray1D(fldValue, elementsDev[1].getClassName()); 
        fldName = LPArray.addValueToArray1D(fldName, ApiMessageReturn.JAVADOC_METHOD_FLDNAME);         fldValue = LPArray.addValueToArray1D(fldValue, elementsDev[1].getMethodName());     
        fldName = LPArray.addValueToArray1D(fldName, ApiMessageReturn.JAVADOC_LINE_FLDNAME);         fldValue = LPArray.addValueToArray1D(fldValue, elementsDev[1].getLineNumber());
        
        for (Integer iNumFields=0;iNumFields<fields.length;iNumFields++){
            if ( (fields[iNumFields]!=null) && (values[iNumFields]!=null) ){
                fldName = LPArray.addValueToArray1D(fldName, fields[iNumFields]);         fldValue = LPArray.addValueToArray1D(fldValue, values[iNumFields]); 
            }
        }
        
        String[] getFilterFldName = new String[0];
        Object[] getFilterFldValue = new Object[0];    
        getFilterFldName = LPArray.addValueToArray1D(getFilterFldName, ApiMessageReturn.JAVADOC_CLASS_FLDNAME);        getFilterFldValue = LPArray.addValueToArray1D(getFilterFldValue, elementsDev[1].getClassName()); 
        getFilterFldName = LPArray.addValueToArray1D(getFilterFldName, ApiMessageReturn.JAVADOC_METHOD_FLDNAME);       getFilterFldValue = LPArray.addValueToArray1D(getFilterFldValue, elementsDev[1].getMethodName());     
        currField = "class_version";
        Integer specialFieldIndex = Arrays.asList(fldName).indexOf(currField);
        if (specialFieldIndex==-1){return;}
        getFilterFldName = LPArray.addValueToArray1D(getFilterFldName, currField);      getFilterFldValue = LPArray.addValueToArray1D(getFilterFldValue, fldValue[specialFieldIndex]);     
        currField = "line_name";
        specialFieldIndex = Arrays.asList(fldName).indexOf(currField);
        if (specialFieldIndex==-1){return;}
        getFilterFldName = LPArray.addValueToArray1D(getFilterFldName, currField);      getFilterFldValue = LPArray.addValueToArray1D(getFilterFldValue, fldValue[specialFieldIndex]);     
        
        String[] getFields = new String[] {"id",ApiMessageReturn.JAVADOC_LINE_FLDNAME,"last_update_on","created_on"};        
        Object[][] diagnoses = Rdbms.getRecordFieldsByFilter(schemaName, tableName, getFilterFldName, getFilterFldValue, getFields);
        if (LAB_FALSE.equalsIgnoreCase(diagnoses[0][0].toString())){        
            Rdbms.insertRecordInTable(schemaName, tableName, fldName, fldValue);
        }else{
            String[] fieldsUpdate = new String[0];
            Object[] fieldsUpdateValue = new Object[0];
            currField = ApiMessageReturn.JAVADOC_LINE_FLDNAME;
            if (elementsDev[1].getLineNumber()!=(Integer) fldValue[Arrays.asList(fldName).indexOf(currField)]){
                fieldsUpdate = LPArray.addValueToArray1D(fieldsUpdate, currField);        fieldsUpdateValue = LPArray.addValueToArray1D(fieldsUpdateValue, elementsDev[1].getLineNumber());                 
            }
            if (fieldsUpdate.length>0){Rdbms.updateRecordFieldsByFilter(schemaName, tableName, fieldsUpdate, fieldsUpdateValue, getFilterFldName, getFilterFldValue);
            }
        }    
    }
/**
 * The schema names are instances per procedure + nature of the data (config/data/requirements...)
 * This method has as a purpose on helping on build the concatenation
 * At the same time it solves the problem on using some symbols like "-" in the name that requires quoted the name
 * If the schemaName is already contained in the procInstanceName it won't be concatenated again.
 * @param procInstanceName String - Basically the Procedure Name
 * @param schemaName String - Which is the nature of the data (config/data/requirements)
 * @return String
 */    
    public static String buildSchemaName(String procInstanceName, String schemaName){
        if (procInstanceName==null) return schemaName; 
        if (procInstanceName.length()>0){
            //Remove this to re-create the schemaName when not called for the first time.
            procInstanceName = procInstanceName.replace("\"", "");
            schemaName = schemaName.replace("\"", "");
            schemaName = schemaName.replace(procInstanceName+"-", "");

            if (!procInstanceName.contains(schemaName)){            
                schemaName = procInstanceName + "-" + schemaName;
                return "\""+schemaName+"\"";
            }else{
                return "\""+procInstanceName+"\"";}
        }
        schemaName = schemaName.replace("\"", "");
        return "\""+schemaName+"\"";                  
    }
    
/**
 * When logging/creating objects that conceptually are mandatory on be part of a structure for a field added or required
 * to get all the fields consider mandatory we invoke the specific parameter field called in the way of "table_name_mandatoryFieldsAction" containing a peer entries in the way of:
 * A call per each mandaotry field to the method mandatoryFieldsByDependency will add the prerrequisites as mandatory too
 * All fields should be in context when the action is performed and not null.The entry is stored in the specific data.properties file for this particular procedure.
 * where the content is expressed in the way of fieldNAmes between spaces where the first field is the one having the prerrequisites. 
      and all different fields separated by pipe, "|".
      Example: project_fieldsAddingMandatoryInsert:analysis method_name method_version*analysis method_name method_version|method_name*analysis method_name method_version|spec*spec spec_code spec_code_version
 * @param procInstanceName - Schema where the template belongs to
 * @param areaName
 * @param fieldNames[] - Fields for the filter to find and get the prerrequisites.
 * @param fieldValues
 * @param tableName. Table where the template is stored in.
 * @param actionName. The action in the database INSERT/UPDATE/DELETE (lowercas preferred).
 * @return String[] All prerrequisite fields for all the fields added to the fieldNames input argument array, when position 3 is set to FALSE then the template is not found.
 */  
    public static Object[][] mandatoryFieldsCheck(String procInstanceName, String areaName, String[] fieldNames, Object[] fieldValues, String tableName, String actionName){
        Object[][] diagnoses = new Object[3][6];
       
        String propertyName = tableName+LpPlatformBusinessRules.TABLE_MANDATORYFIELDS_ACTIONNAME.getTagName()+actionName;
        
        String mandatoryFieldsToCheckDefault = Parameter.getBusinessRuleProcedureFile(procInstanceName.replace("\"", ""), areaName, propertyName+"Default");
        
        String[] mandatoryFields = mandatoryFieldsByDependency(procInstanceName, areaName, fieldNames, tableName, actionName);

        StringBuilder mandatoryFieldsMissing = new StringBuilder(0);
        for (Integer inumLines=0;inumLines<mandatoryFields.length;inumLines++){
            String currField = mandatoryFields[inumLines];
            boolean contains = Arrays.asList(fieldNames).contains(currField.toLowerCase());
            String defValueType = "";
            Object defValueFormat = null;
            if ( (!contains) || (fieldValues[inumLines]==null) ){
                Boolean addIt = false;
                if (mandatoryFieldsToCheckDefault.contains(currField)){
                    Integer endPosic = mandatoryFieldsToCheckDefault.indexOf(currField);
                    String defValue = mandatoryFieldsToCheckDefault.substring(endPosic, mandatoryFieldsToCheckDefault.length());
                    if (defValue.startsWith(currField+"*")){
                        defValue = defValue.replace(currField+"*", "");
                        String[] defValues = defValue.split("\\|");
                        defValue=defValues[0];     
                        defValues = defValue.split("\\*");
                        defValue=defValues[0];     
                        defValueType=defValues[1];                          
                        if (defValue!=null){
                            addIt=true;}
                    }
                    if (addIt){
                        switch (defValueType.toUpperCase()){
                            case "INTEGER":
                                defValueFormat = Integer.parseInt(defValue);
                                break;
                            case "STRING":
                                defValueFormat = defValue;
                                break;
                            default:
                                break;
                        }
                        if (!contains){
                            fieldNames = LPArray.addValueToArray1D(fieldNames, currField);
                            fieldValues = LPArray.addValueToArray1D(fieldValues, defValueFormat);
                        }else{
                            fieldValues[inumLines] = defValueFormat;
                        }    
                    }
                }
                if (!addIt){
                    if (mandatoryFieldsMissing.length()>0){mandatoryFieldsMissing = mandatoryFieldsMissing.append(",");}
                    mandatoryFieldsMissing = mandatoryFieldsMissing.append(currField);
                }
            }        
        }            
        if (mandatoryFieldsMissing.length()>0){
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            diagnoses[0][0]= elements[1].getClassName() + "." + elements[1].getMethodName();
            diagnoses[0][1]= "-999";
            diagnoses[0][2]= "Code Line " + elements[1].getLineNumber();
            diagnoses[0][3]="FALSE";
            diagnoses[0][4]="ERROR:Missing Mandatory Fields";
            diagnoses[0][5]="MissingMandatoryFields"; //"Mandatory fields not found: "+mandatoryFieldsMissing;
            return diagnoses;
        }        
        for (Integer i=0;i<fieldNames.length;i++){
            if (i>=6){diagnoses = LPArray.addColumnToArray2D(diagnoses, "");}
            diagnoses[1][i] = fieldNames[i];}
        for (Integer i=0;i<fieldNames.length;i++){diagnoses[2][i] = fieldValues[i];}
        diagnoses[0][0] = LAB_TRUE;
        return diagnoses;
    }

/**
 * When logging/creating objects that conceptually are mandatory on be part of a structure for a field added or required
 * to get all the fields involved per field we invoke the specific parameter field called in the way of "table_name_fieldsAddingMandatoryAction" containing a peer entries in the way of:
 * The entry is stored in the specific data.properties file for this particular procedure.
 *      where the content is expressed in the way of fieldNAmes between spaces where the first field is the one having the prerrequisites. 
 *      and all different fields separated by pipe, "|".
 *      Example: project_fieldsAddingMandatoryInsert:analysis method_name method_version*analysis method_name method_version|method_name*analysis method_name method_version|spec*spec spec_code spec_code_version
 * @param procInstanceName - Schema where the template belongs to
 * @param fieldNames[] - Fields for the filter to find and get the prerrequisites.
 * @param tableName. Table where the template is stored in.
 * @param actionName. The action in the database INSERT/UPDATE/DELETE (lowercas preferred).
 * @return String[] All prerrequisite fields for all the fields added to the fieldNames input argument array, when position 3 is set to FALSE then the template is not found.
 */   
    public static String[] mandatoryFieldsByDependency(String procInstanceName, String areaName, String[] fieldNames, String tableName, String actionName){
        String propertyName = tableName+LpPlatformBusinessRules.MIDDLEOF_FIELDSADDINGMANDATORY.getTagName()+actionName;
        String mandatoryFieldsByDependency = Parameter.getBusinessRuleProcedureFile(procInstanceName, areaName, propertyName);
        String[] mandatoryByDependency = mandatoryFieldsByDependency.split("\\|");

        for (String currField: fieldNames){
            if ( mandatoryFieldsByDependency.contains(currField) ){
                Integer fieldIndexSpecCode = Arrays.asList(mandatoryByDependency).indexOf(currField);
                if (fieldIndexSpecCode!=-1){
                    String[] propertyEntryValue = mandatoryByDependency[fieldIndexSpecCode].split("\\*");
                    if ( (propertyEntryValue.length==2) && (Arrays.asList(propertyEntryValue[0]).contains(currField.toLowerCase())) ){
                        String[] fieldToAddByDependency = propertyEntryValue[1].split(" ");
                        for (String fAdd: fieldToAddByDependency){
                            if (Arrays.asList(fieldNames).indexOf(fAdd)==-1){
                                fieldNames = LPArray.addValueToArray1D(fieldNames, fAdd);
                            }    
                        }    
                    }
                }    
            }        
        }            
        return fieldNames;
    }

/**
 * When logging/creating objects that conceptually requires one template to define its nature then one call to this method is required
 * to get all those parameters and log/create the new instance accordingly.
 * Each procedure has a specific parameter field called in the way of "procedureName-config" containing a peer entries in the way of:
 *      tableName__configTableName = Specify the table name where the template is stored.
 *      tableName_configTableKeyFields = Specify the mandatory fields that should be present in the peer fieldNames/fieldValues
 *                                       to link the new object with its template in the proper and expected way. 
 * @param schemaName - Schema where the template belongs to
 * @param fieldNames - Fields for the filter to find and get the proper template.
 * @param fieldValues - Values for the fields described above.
 * @param tableName. Table where the template is stored in.
 * @return String[] when position 3 is set to FALSE then the template is not found.
 */   
    public static Object[] configObjectExists( String procInstanceName, String[] fieldNames, Object[] fieldValues, String tableName){
        String errorCode = ""; 
        Object[] errorDetailVariables = new Object[0];        
        
        String configTableName = Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.SUFFIX_CONFIGTABLENAME.getAreaName(), tableName+LpPlatformBusinessRules.SUFFIX_CONFIGTABLENAME.getTagName());
        String configTableKeyFields = Parameter.getBusinessRuleProcedureFile(procInstanceName, LpPlatformBusinessRules.SUFFIX_CONFIGTABLEKEYFIELDS.getAreaName(), tableName+LpPlatformBusinessRules.SUFFIX_CONFIGTABLEKEYFIELDS.getTagName());

        String[] configTableKeyFieldName = configTableKeyFields.split("\\|");
        Object[] configTableKeyFielValue = new Object[0];
        
        StringBuilder missingFieldInArray = new StringBuilder(0);
        for (Integer i=0;i<configTableKeyFieldName.length;i++){
            
            String currField = configTableKeyFieldName[i];
            String[] currFields = currField.split("\\*");
            currField=currFields[0];     
            String currFieldType =currFields[1];              
            Integer fieldPosic = Arrays.asList(fieldNames).indexOf(currField);            
            if (fieldPosic==-1){
                if (missingFieldInArray.length()>0){missingFieldInArray=missingFieldInArray.append(", ");}
                missingFieldInArray = missingFieldInArray.append(currField);                
            }else{
                Object currFieldInFormat = null;
                switch (currFieldType.toUpperCase()){
                    case "INTEGER":
                        currFieldInFormat = Integer.parseInt(fieldValues[fieldPosic].toString());
                        break;
                    case "STRING":
                        currFieldInFormat = fieldValues[fieldPosic].toString();
                        break;
                    default:
                        break;
                }                
                configTableKeyFieldName[i] = currField;
                configTableKeyFielValue = LPArray.addValueToArray1D(configTableKeyFielValue, currFieldInFormat);
            }
                
        }       
        Object[] diagnosis = Rdbms.existsRecord(procInstanceName, configTableName, configTableKeyFieldName, configTableKeyFielValue);
        if (!LAB_TRUE.equalsIgnoreCase(diagnosis[0].toString())){            
           String[] configTableFilter = LPArray.joinTwo1DArraysInOneOf1DString(configTableKeyFieldName, configTableKeyFielValue, LPPlatform.AUDIT_FIELDS_UPDATED_SEPARATOR);
           return ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.MISSINGTABLECONFIGCODE, new Object[]{tableName, Arrays.toString(configTableFilter), procInstanceName, diagnosis[5]});
        }    

        
        return diagnosis;
    }
/**
 * In some cases the field value requires check a kind of logic to verify that the value is aligned with a particular business rule.When this is required then a peer should be added to the properties field
 Each procedure has a specific parameter field called in the way of "procedureName-config" containing a peer entries in the way of: 
      tableName+"_specialFieldsCheck - Specify the field having this need.
 * tableName+"_specialFieldsCheck_methodName - The method to be invoked that contains the logic.
 * @param procInstanceName - String - Procedure
     * @param areaName
 * @param fieldNames - String[] - fields involved in the actionName being performed
 * @param fieldValues - Object[] - field values 
 * @param tableName - String - Table Name
 * @param actionName - String - action being performed
 * @return String[] - Returns detailed info about the evaluation and where it ends, position 3 set to TRUE means all is ok otherwise FALSE.
 */    
    public String[] specialFieldsCheck(String procInstanceName, String areaName, String[] fieldNames, Object[] fieldValues, String tableName, String actionName){
        Object[] errorDetailVariables = new Object[0];        
        
        String specialFieldName = Parameter.getBusinessRuleProcedureFile(procInstanceName, areaName, tableName+LpPlatformBusinessRules.SUFFIX_SPECIALFIELDNAME.getTagName());
        String specialFieldMethodName = Parameter.getBusinessRuleProcedureFile(procInstanceName, areaName, tableName+LpPlatformBusinessRules.SUFFIX_SPECIALFIELDMETHODNAME.getTagName());

        String[] specialFields = specialFieldName.split("\\|");
        String[] specialFieldsMethods = specialFieldMethodName.split("\\|");
        Integer specialFieldIndex = -1;
        
        for (Integer inumLines=0;inumLines<fieldNames.length;inumLines++){
            String currField = fieldNames[inumLines];
            boolean contains = Arrays.asList(specialFields).contains(currField);
            if (contains){                    
                    try {                    
                        specialFieldIndex = Arrays.asList(specialFields).indexOf(currField);
                        String aMethod = specialFieldsMethods[specialFieldIndex];
                        Method method = null;
                        
                        Class<?>[] paramTypes = {Rdbms.class, String[].class, Object[].class, String.class};
                        method = getClass().getDeclaredMethod(aMethod, paramTypes);
                        
                        Object specialFunctionReturn = LPNulls.replaceNull(method.invoke(this, fieldNames, fieldValues, procInstanceName));
                        if (specialFunctionReturn.toString().contains("ERROR")) {
                            return (String[]) ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.SPECIALFUNCTION_RETURNEDERROR, new Object[]{currField, aMethod, specialFunctionReturn.toString()});
                        }
                    } catch (NoSuchMethodException | SecurityException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
                        return (String[]) ApiMessageReturn.trapMessage(LAB_FALSE, LpPlatformErrorTrapping.SPECIALFUNCTION_CAUSEDEXCEPTION, new Object[]{currField, ex.getCause(), ex.getMessage()});
                    }
            }
        }         
        errorDetailVariables = LPArray.addValueToArray1D(errorDetailVariables, specialFieldName.replace("\\|", ", "));
        return (String[]) ApiMessageReturn.trapMessage(LAB_TRUE, LpPlatformSuccess.SPECIALFUNCTION_ALLSUCCESS, errorDetailVariables);                      
    }
    
/**
 * trapMessage is the method that should reduce the lines of code for justifying lines of code against its requirement
 to keep the track about which is the requirement covered by each section in each method.
 * When running the code in Dev-Mode then it should mark as 'covered' the existing requirement or create one record for this given requirement
 * The parameter.config.testing-html-settings mandatoryFields_requerimentsJavaDoc defines which are the mandatory fields that should be added
 * to the peer fields/values to let this call be consider fill enough to proceed.
 * 
 */
    public static final Integer TRAP_MESSAGE_EVALUATION_POSIC=0;
    public static final Integer TRAP_MESSAGE_CODE_POSIC=4;
    public static final Integer TRAP_MESSAGE_MESSAGE_POSIC=6;
    
    
    /**
     *
     * @param strBuilder
     * @param strToReplace
     * @param newString
     * @return
     */
    public static StringBuilder replaceStringBuilderByStringAllReferences(StringBuilder strBuilder, String strToReplace, String newString){
        int start = strBuilder.indexOf(strToReplace);
        while (start != -1 ){
            int end = start + strToReplace.length();
            strBuilder.replace(start, end, newString);
            start = strBuilder.indexOf(strToReplace);
        }   
        return strBuilder;
    }
    
    /**
     *
     * @param query
     * @param queryParams
     * @param callerInfo
     * @param msgCode
     * @param msgVariables
     */
    public static void saveMessageInDbErrorLog(String query, Object[] queryParams, Object[] callerInfo, String msgCode, Object[] msgVariables) {          
        saveMessageInDbErrorLog(query, queryParams, callerInfo, msgCode, msgVariables, null);
    }
    public static void saveMessageInDbErrorLog(String query, Object[] queryParams, Object[] callerInfo, String msgCode, Object[] msgVariables, String procName) {          
    //    if (1==1) return;
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();
        if (procInstanceName==null) procInstanceName=procName;
        if (procInstanceName==null) return;
        if (!Rdbms.getRdbms().getIsStarted()){
//            Logger.log(LogTag.JFR, LogLevel.TRACE, msgCode);
            return;
        }
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString())){
            procInstanceName = "";
            dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))
                return;
        }

        String[] fldNames=new String[]{TblsCnfg.zzzDbErrorLog.CREATION_DATE.getName(), TblsCnfg.zzzDbErrorLog.QUERY.getName(), TblsCnfg.zzzDbErrorLog.QUERY_PARAMETERS.getName(),
        TblsCnfg.zzzDbErrorLog.ERROR_MESSAGE.getName(), TblsCnfg.zzzDbErrorLog.CLASS_CALLER.getName(), TblsCnfg.zzzDbErrorLog.RESOLVED.getName()};
        Object[] fldValues=new Object[]{LPDate.getCurrentTimeStamp(), query, Arrays.toString(queryParams), msgCode, Arrays.toString(callerInfo), false};
        String actionName=ProcedureRequestSession.getInstanceForActions(null, null, null).getActionName();
        if (actionName!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsCnfg.zzzPropertiesMissing.ACTION_NAME.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, actionName);
        }        
        Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_DB_ERROR.getTableName(), 
            fldNames, fldValues);
  }    
  
    /**
     *
     * @param schemaName
     * @param fileName
     * @param callerInfo
     * @param paramName
     */
    public static void saveParameterPropertyInDbErrorLog(String schemaName, String fileName, Object[] callerInfo, String paramName, Boolean isOptional) {          
        if (Boolean.valueOf(isOptional)) return;
        if (!Rdbms.getRdbms().getIsStarted()){
            //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, paramName);
            return;
        }
        String procInstanceName = LPNulls.replaceNull(schemaName);
        Object[] dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getTableName());
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))
            procInstanceName = "";
        else{
            dbTableExists = Rdbms.dbTableExists(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()) , TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getTableName());
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(dbTableExists[0].toString()))
                return;
        }        
        String[] fldNames=new String[]{TblsCnfg.zzzPropertiesMissing.CREATION_DATE.getName(), TblsCnfg.zzzPropertiesMissing.AREA.getName(),
            TblsCnfg.zzzPropertiesMissing.RULE_NAME.getName(), TblsCnfg.zzzPropertiesMissing.CLASS_CALLER.getName()};
        Object[] fldValues=new Object[]{LPDate.getCurrentTimeStamp(), fileName, paramName, Arrays.toString(callerInfo)};
        if (procInstanceName!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsCnfg.zzzPropertiesMissing.PROCEDURE.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, procInstanceName);
        }
        String actionName=ProcedureRequestSession.getInstanceForActions(null, null, null).getActionName();
        if (actionName!=null){
            fldNames=LPArray.addValueToArray1D(fldNames, TblsCnfg.zzzPropertiesMissing.ACTION_NAME.getName());
            fldValues=LPArray.addValueToArray1D(fldValues, actionName);
        }
        Object[] insertRecordInTable = Rdbms.insertRecordInTable(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName()), TblsCnfg.TablesConfig.ZZZ_PROPERTIES_ERROR.getTableName(), 
                fldNames, fldValues);
    }      
    public static Object[] isProcedureBusinessRuleEnable(String procName, String fileSchemaRepository, String ruleName){
        String enableValuesStr=getBusinessRuleAppFile("businessRulesEnableValues", true); 
        String[] enableRuleValues=enableValuesStr.split("\\|");
        String ruleValue=Parameter.getBusinessRuleProcedureFile(procName, fileSchemaRepository, ruleName);
        if (ruleValue.length()==0) 
            return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.BUS_RUL_REVIEWBYTESTINGGROUP_NOT_FOUND, null);
        for (String curVal: enableRuleValues){
            if (curVal.equalsIgnoreCase(ruleValue))
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformErrorTrapping.RULE_NAME_VALUE, new Object[]{ruleName, ruleValue});        
        }
        return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LpPlatformErrorTrapping.RULE_NAME_VALUE, new Object[]{ruleName, ruleValue});
    }
}