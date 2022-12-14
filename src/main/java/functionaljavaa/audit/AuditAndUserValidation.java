/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.features.Token;
import functionaljavaa.businessrules.BusinessRules;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPAPIArguments.buildAPIArgsumentsArgsValues;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import org.json.simple.JSONArray;
import trazit.enums.EnumIntBusinessRules;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;
import trazit.session.ApiMessageReturn;
import trazit.session.ProcedureRequestSession;
import trazit.session.ResponseMessages;

    

/**
 *
 * @author User
 */
public class AuditAndUserValidation {

    public enum AuditAndUserValidationErrorTrapping implements EnumIntMessages{ 
        WRONG_PHRASE ("wrongAuditReasonPhrase", "", ""),
        PROC_INSTANCE_NAME_NULL ("procInstanceNameNull", "", ""),
        ;
        private AuditAndUserValidationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    public enum AuditAndUserValidationBusinessRules implements EnumIntBusinessRules{     
        PREFIX_AUDITREASONPHRASE ("AuditReasonPhrase", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|', null, null),
        ;
        private AuditAndUserValidationBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator
        , Boolean isOpt, ArrayList<String[]> preReqs){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
            this.isOptional=isOpt;
            this.preReqs=preReqs;
        }       
        @Override        public String getTagName(){return this.tagName;}
        @Override        public String getAreaName(){return this.areaName;}
        @Override        public JSONArray getValuesList(){return this.valuesList;}
        @Override        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        @Override        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        @Override        public Boolean getIsOptional() {return isOptional;}
        @Override        public ArrayList<String[]> getPreReqs() {return this.preReqs;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        
        private final Boolean isOptional;
        private final ArrayList<String[]> preReqs;
    }

    private static AuditAndUserValidation auditUserVal;

     public static AuditAndUserValidation getInstanceForActions(HttpServletRequest request, HttpServletResponse response, String language, BusinessRules busRulesProcInstance, Boolean isPlatform) { 
        if (auditUserVal == null) {
            if (request==null) return null;
            auditUserVal = new AuditAndUserValidation(request, response, language, busRulesProcInstance, isPlatform);
            return auditUserVal;
        } else {
         return auditUserVal;
        }  
    }
    public void killInstance(){
        auditUserVal=null;
    }     
       /**
     * @return the auditReasonPhrase
     */
    public String getAuditReasonPhrase() {
        return auditReasonPhrase;
    }

    /**
     * @return the checkUserValidationPassesDiag
     */
    public Object[] getCheckUserValidationPassesDiag() {
        return checkUserValidationPassesDiag;
    }
    
    private String auditReasonPhrase="";
    private Object[] checkUserValidationPassesDiag;
    
    private AuditAndUserValidation(HttpServletRequest request, HttpServletResponse response, String language, BusinessRules busRulesProcInstance, Boolean isPlatform){
        
        String[] mandatoryParams = new String[]{};
        LPAPIArguments[] argsDef=null;
        if (isPlatform)
            argsDef=new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 6),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)};
        else{
            argsDef=new LPAPIArguments[]{
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), false, 8),
                new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6)};
                
        }
        Object[] requestArgValues=buildAPIArgsumentsArgsValues(request, argsDef);
        String actionName=requestArgValues[0].toString();
        String finalToken=requestArgValues[1].toString();
        String procInstanceName=null;
        if (!isPlatform)
            procInstanceName=requestArgValues[2].toString();
        //String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
        //String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        //String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
            this.checkUserValidationPassesDiag=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN, null);                             
            return;
        }      
        if (actionName==null){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, "notActionPassed", null);
            return;
        }
        BusinessRules bi=new BusinessRules(procInstanceName, null);   
        Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(procInstanceName, actionName, bi);
        if (procActionRequiresUserConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)){     
            if (!procActionRequiresUserConfirmation[0].toString().equalsIgnoreCase(LPPlatform.LAB_TRUE))
                mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);                
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
            isValidAuditPhrase(procInstanceName, actionName, auditReasonPhrase, busRulesProcInstance);
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(procInstanceName, actionName, bi);
        if (procActionRequiresEsignConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)){      
            if (!procActionRequiresEsignConfirmation[0].toString().equalsIgnoreCase(LPPlatform.LAB_TRUE))
                mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);                
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
            isValidAuditPhrase(procInstanceName, actionName, auditReasonPhrase, busRulesProcInstance);
        }        
        Object[] procActionRequiresJustificationPhrase = LPPlatform.procActionRequiresJustificationPhrase(procInstanceName, actionName, bi);
        if (procActionRequiresJustificationPhrase[0].toString().contains(LPPlatform.LAB_TRUE)){      
            if (!procActionRequiresJustificationPhrase[0].toString().equalsIgnoreCase(LPPlatform.LAB_TRUE))
                mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);
            String auditReasonPhrase = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);      
            isValidAuditPhrase(procInstanceName, actionName, auditReasonPhrase, busRulesProcInstance);
        }        
        
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatoryParams);                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            ResponseMessages messages = ProcedureRequestSession.getInstanceForActions(null, null, null).getMessages();
            messages.addMainForError(LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()});
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()});                             
            return;
        }

        if (LPArray.valueInArray(mandatoryParams , GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE)){
            this.auditReasonPhrase=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE); 
            if (!isValidAuditPhrase(procInstanceName, actionName, this.auditReasonPhrase, busRulesProcInstance)) return;                
        }

        if ( (procActionRequiresUserConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_USER_VERIFICATION, new Object[]{});                             
            return;            
        }
        
        if ( (procActionRequiresEsignConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_ESIGN, new Object[]{});                             
            return;
        }
        this.auditReasonPhrase=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE); 
        this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.AUDIT_AND_USER_VALIDATION_CHECK_SUCCESS, null);
    }
    private Boolean isValidAuditPhrase(String procInstanceName, String actionName, String auditReasonPhrase, BusinessRules busRulesProcInstance){
//        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        if (procInstanceName==null){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuditAndUserValidationErrorTrapping.PROC_INSTANCE_NAME_NULL, null);
            return false;
        }
        String[] actionAuditReasonInfo = busRulesProcInstance.getProcedureBusinessRule(actionName+AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getTagName()).split("\\|");
        //String[] actionAuditReasonInfo = Parameter.getBusinessRuleProcedureFile(procInstanceName.replace("\"", ""), AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getAreaName(), actionName+AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getTagName()).split("\\|");
        if ( ("LIST".equalsIgnoreCase(actionAuditReasonInfo[0])) && (!LPArray.valueInArray(actionAuditReasonInfo, auditReasonPhrase)) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuditAndUserValidationErrorTrapping.WRONG_PHRASE, new Object[]{auditReasonPhrase, Arrays.toString(actionAuditReasonInfo)});
            return false;
        }
        return true;
    }
}
