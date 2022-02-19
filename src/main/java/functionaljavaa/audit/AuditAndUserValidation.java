/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Token;
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
        CHECK_SUCCESS ("checkUserValidationPassesSuccess", "", ""),
        WRONG_PHRASE ("wrongAuditReasonPhrase", "", ""),
        PROC_INSTANCE_NAME_NULL ("procInstanceNameNull", "", ""),
        ;
        private AuditAndUserValidationErrorTrapping(String errCode, String defaultTextEn, String defaultTextEs){
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
    public enum AuditAndUserValidationBusinessRules implements EnumIntBusinessRules{     
        PREFIX_AUDITREASONPHRASE ("AuditReasonPhrase", GlobalVariables.Schemas.PROCEDURE.getName(), null, null, '|'),
        ;
        private AuditAndUserValidationBusinessRules(String tgName, String areaNm, JSONArray valuesList, Boolean allowMulti, char separator){
            this.tagName=tgName;
            this.areaName=areaNm;
            this.valuesList=valuesList;  
            this.allowMultiValue=allowMulti;
            this.multiValueSeparator=separator;
        }       
        public String getTagName(){return this.tagName;}
        public String getAreaName(){return this.areaName;}
        public JSONArray getValuesList(){return this.valuesList;}
        public Boolean getAllowMultiValue(){return this.allowMultiValue;}
        public char getMultiValueSeparator(){return this.multiValueSeparator;}
        
        private final String tagName;
        private final String areaName;
        private final JSONArray valuesList;  
        private final Boolean allowMultiValue;
        private final char multiValueSeparator;        

        @Override
        public Boolean getIsOptional() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ArrayList<String[]> getPreReqs() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static AuditAndUserValidation auditUserVal;

     public static AuditAndUserValidation getInstanceForActions(HttpServletRequest request, HttpServletResponse response, String language, BusinessRules busRulesProcInstance) { 
        if (auditUserVal == null) {
            if (request==null) return null;
            auditUserVal = new AuditAndUserValidation(request, response, language, busRulesProcInstance);
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
    
    private AuditAndUserValidation(HttpServletRequest request, HttpServletResponse response, String language, BusinessRules busRulesProcInstance){
        
        String[] mandatoryParams = new String[]{};
        
        LPAPIArguments[] argsDef=new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)};

        Object[] requestArgValues=buildAPIArgsumentsArgsValues(request, argsDef);
        String procInstanceName=requestArgValues[0].toString();
        String actionName=requestArgValues[1].toString();
        String finalToken=requestArgValues[2].toString();
        //String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
        //String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        //String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
            this.checkUserValidationPassesDiag=ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null);                             
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
            messages.addMainForError(LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()});
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()});                             
            return;
        }

        if (LPArray.valueInArray(mandatoryParams , GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE)){
            this.auditReasonPhrase=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE); 
            if (!isValidAuditPhrase(procInstanceName, actionName, this.auditReasonPhrase, busRulesProcInstance)) return;                
        }

        if ( (procActionRequiresUserConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_USER_VERIFICATION.getName(), new Object[]{});                             
            return;            
        }
        
        if ( (procActionRequiresEsignConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_ESIGN.getName(), new Object[]{});                             
            return;
        }
        this.auditReasonPhrase=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE); 
        this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, AuditAndUserValidationErrorTrapping.CHECK_SUCCESS.getErrorCode(), null);
    }
    private Boolean isValidAuditPhrase(String procInstanceName, String actionName, String auditReasonPhrase, BusinessRules busRulesProcInstance){
//        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        if (procInstanceName==null){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuditAndUserValidationErrorTrapping.PROC_INSTANCE_NAME_NULL.getErrorCode(), null);
            return false;
        }
        String[] actionAuditReasonInfo = busRulesProcInstance.getProcedureBusinessRule(actionName+AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getTagName()).split("\\|");
        //String[] actionAuditReasonInfo = Parameter.getBusinessRuleProcedureFile(procInstanceName.replace("\"", ""), AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getAreaName(), actionName+AuditAndUserValidationBusinessRules.PREFIX_AUDITREASONPHRASE.getTagName()).split("\\|");
        if ( ("LIST".equalsIgnoreCase(actionAuditReasonInfo[0])) && (!LPArray.valueInArray(actionAuditReasonInfo, auditReasonPhrase)) ){
            this.checkUserValidationPassesDiag= ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, AuditAndUserValidationErrorTrapping.WRONG_PHRASE.getErrorCode(), new Object[]{auditReasonPhrase, Arrays.toString(actionAuditReasonInfo)});
            return false;
        }
        return true;
    }
}
