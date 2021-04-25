/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functionaljavaa.audit;

import com.labplanet.servicios.app.GlobalAPIsParams;
import databases.Token;
import functionaljavaa.parameter.Parameter;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import static lbplanet.utilities.LPAPIArguments.buildAPIArgsumentsArgsValues;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import static lbplanet.utilities.LPPlatform.CONFIG_PROC_FILE_NAME;
import trazit.session.ProcedureRequestSession;

    

/**
 *
 * @author User
 */
public class AuditAndUserValidation {
    private static AuditAndUserValidation auditUserVal;

     public static AuditAndUserValidation getInstanceForActions(HttpServletRequest request, HttpServletResponse response, String language) { 
        if (auditUserVal == null) {
            if (request==null) return null;
            auditUserVal = new AuditAndUserValidation(request, response, language);
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
    
    private AuditAndUserValidation(HttpServletRequest request, HttpServletResponse response, String language){
                
        String[] mandatoryParams = new String[]{};
        
        LPAPIArguments[] argsDef=new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX, LPAPIArguments.ArgumentType.STRING.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME, LPAPIArguments.ArgumentType.STRING.toString(), false, 7),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN, LPAPIArguments.ArgumentType.STRING.toString(), false, 7)};

        Object[] requestArgValues=buildAPIArgsumentsArgsValues(request, argsDef);
        String procInstanceName=requestArgValues[0].toString();
        String actionName=requestArgValues[1].toString();
        String finalToken=requestArgValues[2].toString();
        //String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        //String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        //String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
            this.checkUserValidationPassesDiag=LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null);                             
            return;
        }      
        if (actionName==null){
            this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "", null);
            return;
        }
                    
        Object[] procActionRequiresUserConfirmation = LPPlatform.procActionRequiresUserConfirmation(procInstanceName, actionName);
        if (procActionRequiresUserConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)){     
            if (!procActionRequiresUserConfirmation[0].toString().equalsIgnoreCase(LPPlatform.LAB_TRUE))
                mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);                
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_USER_TO_CHECK);    
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_PSWD_TO_CHECK);    
        }
        Object[] procActionRequiresEsignConfirmation = LPPlatform.procActionRequiresEsignConfirmation(procInstanceName, actionName);
        if (procActionRequiresEsignConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)){      
            if (!procActionRequiresEsignConfirmation[0].toString().equalsIgnoreCase(LPPlatform.LAB_TRUE))
                mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE);                
            mandatoryParams = LPArray.addValueToArray1D(mandatoryParams, GlobalAPIsParams.REQUEST_PARAM_ESIGN_TO_CHECK);    
        }        
        
        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, mandatoryParams);                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()});                             
            return;
        }

        if (LPArray.valueInArray(mandatoryParams , GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE)){
            this.auditReasonPhrase=request.getParameter(GlobalAPIsParams.REQUEST_PARAM_AUDIT_REASON_PHRASE); 
            if (!isValidAuditPhrase(actionName, this.auditReasonPhrase)) return;                
        }

        if ( (procActionRequiresUserConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&     
             (!LPFrontEnd.servletUserToVerify(request, response, token.getUserName(), token.getUsrPw())) ){
            this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()});                             
            return;            
        }
        
        if ( (procActionRequiresEsignConfirmation[0].toString().contains(LPPlatform.LAB_TRUE)) &&    
             (!LPFrontEnd.servletEsignToVerify(request, response, token.geteSign())) ){
            this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()});                             
            return;
        }
    
        this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "checkUserValidationPassesSuccess", null);
    }
    private Boolean isValidAuditPhrase(String actionName, String auditReasonPhrase){
        String procInstanceName=ProcedureRequestSession.getInstanceForActions(null, null, null).getProcedureInstance();        
        String[] actionAuditReasonInfo = Parameter.getMessageCodeValue(procInstanceName.replace("\"", "")+CONFIG_PROC_FILE_NAME, actionName+"AuditReasonPhrase").split("\\|");
        if ( ("LIST".equalsIgnoreCase(actionAuditReasonInfo[0])) && (!LPArray.valueInArray(actionAuditReasonInfo, auditReasonPhrase)) ){
            this.checkUserValidationPassesDiag= LPPlatform.trapMessage(LPPlatform.LAB_FALSE, "wrongAuditReasonPhrase", new Object[]{auditReasonPhrase, Arrays.toString(actionAuditReasonInfo)});
            return false;
        }
        return true;
    }
}
