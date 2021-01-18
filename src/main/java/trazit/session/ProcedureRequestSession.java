/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;

/**
 *
 * @author User
 */
public class ProcedureRequestSession {
    
    private static ProcedureRequestSession theSession;
    private String procedureInstance;
    private String actionName;
    private Token token;
    private String tokenStr;
    private String language;
    private Boolean isForTesting;
    private Boolean hasErrors;
    private String errorMessage;
    private AuditAndUserValidation auditAndUsrValid;
    
    
    private ProcedureRequestSession(HttpServletRequest request, HttpServletResponse response, Boolean isForTesting, Boolean isForUAT, Boolean isFrontend){
        try{
        if (request==null) return;
        this.language = LPFrontEnd.setLanguage(request); 
        this.isForTesting=isForTesting;
        
        if (!isForTesting){
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                this.hasErrors=true;
                return;          
            }                     
            String actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            this.actionName=actionNm;
        }
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        if (!isForUAT){
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);   
            if (finalToken!=null){
                Token tokn = new Token(finalToken);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                        this.hasErrors=true;
                        return;                             
                }
                this.token=tokn;
                this.tokenStr=finalToken;
            }
            this.procedureInstance=procInstanceName;
        }
        if (!isForTesting && !isForUAT && !isFrontend){
            Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, token, actionName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                return ;                           
            }            
            actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, actionEnabled);
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                return ;                           
            }                        
            AuditAndUserValidation auditAndUsrVal=AuditAndUserValidation.getInstanceForActions(request, null, language);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrVal.getCheckUserValidationPassesDiag()[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, auditAndUsrVal.getCheckUserValidationPassesDiag());              
                this.hasErrors=true;
                this.errorMessage=auditAndUsrVal.getCheckUserValidationPassesDiag()[auditAndUsrVal.getCheckUserValidationPassesDiag().length-1].toString();
                return;          
            }     
            this.auditAndUsrValid=auditAndUsrVal;
            String schemaConfigName=LPPlatform.buildSchemaName(procInstanceName, LPPlatform.SCHEMA_CONFIG);
            Rdbms.setTransactionId(schemaConfigName);
        }            
        Rdbms.stablishDBConection();
        if (!LPFrontEnd.servletStablishDBConection(request, response)){return;}
        this.hasErrors=false;
        }catch(Exception e){
            this.hasErrors=true;
            this.errorMessage=e.getMessage();
        }
    }
    
    public void killIt(){
        this.theSession=null;
        this.actionName=null;
        this.token=null;
        this.isForTesting=null;
        this.procedureInstance=null;
        Rdbms.closeRdbms(); 
    }
    
    public String getActionName(){
        return this.actionName;
    }
    public String getLanguage(){
        return this.language;
    }
    public Token getToken(){
        return this.token;
    }
    public String getTokenString(){
        return this.tokenStr;
    }
    public String getProcedureInstance(){
        return this.procedureInstance;
    }
    public Boolean getIsForTesting(){
        if (this.isForTesting==null)return false;
        return this.isForTesting;
    }    
    public Boolean getHasErrors(){
        if (this.hasErrors==null)return true;
        return this.hasErrors;
    }    
    public String getErrorMessage(){
        if (this.errorMessage==null)return "";
        return this.errorMessage;
    }    
    public AuditAndUserValidation getAuditAndUsrValid(){
        return this.auditAndUsrValid;
    }
   
    
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        if (theSession==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, true);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        if (theSession==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        if (theSession==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, true, false);
        }
        return theSession;
    }

}
