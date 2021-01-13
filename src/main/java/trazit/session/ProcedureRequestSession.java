/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import javax.servlet.http.HttpServletRequest;
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
    private String language;
    private AuditAndUserValidation auditAndUsrValid;
    
    private ProcedureRequestSession(HttpServletRequest request){
        this.language = LPFrontEnd.setLanguage(request); 
        
        String[] errObject = new String[]{"Servlet programAPI at " + request.getServletPath()};   
        
        String[] mandatoryParams = new String[]{""};
        Object[] areMandatoryParamsInResponse = LPHttp.areAPIMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, null, 
                LPPlatform.API_ERRORTRAPING_MANDATORY_PARAMS_MISSING, new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
            return;          
        }             
        
        String schemaPrefix = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_SCHEMA_PREFIX);            
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, null, 
                        LPPlatform.API_ERRORTRAPING_INVALID_TOKEN, null, language);              
                return;                             
        }
        this.token=token;
        this.procedureInstance=schemaPrefix;
        this.actionName=actionName;
        
        Object[] actionEnabled = LPPlatform.procActionEnabled(schemaPrefix, token, actionName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, actionEnabled);
            return ;                           
        }            
        actionEnabled = LPPlatform.procUserRoleActionEnabled(schemaPrefix, token.getUserRole(), actionName);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, actionEnabled);
            return ;                           
        }                        
        AuditAndUserValidation auditAndUsrValid=AuditAndUserValidation.getInstance(request, null, language);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())){
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, auditAndUsrValid.getCheckUserValidationPassesDiag());              
            return;          
        }     
        this.auditAndUsrValid=auditAndUsrValid;

        if (!LPFrontEnd.servletStablishDBConection(request, null, false)){return;}
        
    }
    
    public void killIt(){
        this.theSession=null;
        this.actionName=null;
        this.token=null;
        this.procedureInstance=null;
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
    public String getProcedureInstance(){
        return this.procedureInstance;
    }
    public AuditAndUserValidation getAuditAndUsrValid(){
        return this.auditAndUsrValid;
    }

    
    
    public static ProcedureRequestSession getInstance(HttpServletRequest req){
        if (theSession==null){
            theSession=new ProcedureRequestSession(req);
        }
        return theSession;
    }
}
