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
import functionaljavaa.testingscripts.TestingAuditIds;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import functionaljavaa.testingscripts.TestingMessageCodeVisited;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;

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
    private Boolean isForQuery;
    private Boolean hasErrors;
    private String errorMessage;
    private AuditAndUserValidation auditAndUsrValid;
    private TestingAuditIds tstAuditObj;
    private TestingBusinessRulesVisited busRuleVisited;
    private TestingMessageCodeVisited msgCodeVisited;
    
    private ProcedureRequestSession(HttpServletRequest request, HttpServletResponse response, Boolean isForTesting, Boolean isForUAT, Boolean isQuery, String theActionName){
        try{
        if (request==null) return;
        this.language = LPFrontEnd.setLanguage(request); 
        this.isForTesting=isForTesting;
        
        String dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);            
        if (dbName==null || dbName.length()==0)        
            Rdbms.stablishDBConection();
        else
            Rdbms.stablishDBConection(dbName);
        if (!LPFrontEnd.servletStablishDBConection(request, response)){
            this.hasErrors=true;
            this.errorMessage="db connection not stablished";
            return;
        }        
        if (!isForTesting){
            Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language);              
                this.hasErrors=true;
                return;          
            }                 
            String actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            this.actionName=actionNm;
        }else
            this.actionName=theActionName;
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
        if (!isForUAT){
            String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);   
            if (finalToken!=null){
                Token tokn = new Token(finalToken);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())){
                        LPFrontEnd.servletReturnResponseError(request, response, 
                                LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, language);              
                        this.hasErrors=true;
                        return;                             
                }
                this.token=tokn;
                this.tokenStr=finalToken;
            }
            this.procedureInstance=procInstanceName;
        }
        if (!isForTesting && !isForUAT && !isQuery){  
            Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, token, actionName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, actionEnabled);
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                return ;                           
            }            
            actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, null, actionEnabled);
                return ;                           
            }                        
        }
        if (!isForTesting && !isForUAT && !isQuery){            
            AuditAndUserValidation auditAndUsrVal=AuditAndUserValidation.getInstanceForActions(request, null, language);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(auditAndUsrVal.getCheckUserValidationPassesDiag()[0].toString())){
                this.hasErrors=true;
                this.errorMessage=auditAndUsrVal.getCheckUserValidationPassesDiag()[auditAndUsrVal.getCheckUserValidationPassesDiag().length-1].toString();
                //LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, auditAndUsrVal.getCheckUserValidationPassesDiag());              
                return;          
            }     
            this.auditAndUsrValid=auditAndUsrVal;
            String schemaConfigName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
            Rdbms.setTransactionId(schemaConfigName);
        }
        if (isForTesting){
            this.tstAuditObj = TestingAuditIds.getInstance();
            this.busRuleVisited = TestingBusinessRulesVisited.getInstance();
            this.msgCodeVisited = TestingMessageCodeVisited.getInstance();
        }
        this.isForQuery=isForQuery;
        this.hasErrors=false;
        }catch(Exception e){
            this.hasErrors=true;
            this.errorMessage=e.getMessage();
        }
    }
    
    public void killIt(){
//        if (!this.isForQuery) 
            this.theSession=null;
        if (this.isForQuery!=null && !this.isForQuery) this.token=null;
        this.actionName=null;
        this.isForTesting=null;
        this.procedureInstance=null;
        if (tstAuditObj!=null)
            tstAuditObj.killIt();
        if (busRuleVisited!=null)
            busRuleVisited.killIt();
        if (msgCodeVisited!=null)
            msgCodeVisited.killIt();
        
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
    public TestingAuditIds getTestingAuditObj(){
        return this.tstAuditObj;
    }
    public TestingBusinessRulesVisited getTestingBusinessRulesVisitedObj(){
        return this.busRuleVisited;
    }
    public TestingMessageCodeVisited getTestingMessageCodeVisitedObj(){
        return this.msgCodeVisited;
    }
    
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, true, null);
        }            
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
/*        if (isTesting){
            Boolean passCheckers=true;
            String actionNm = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            String procInstanceName = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
            String finalToken = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            Token tokn = null;
            if (finalToken!=null){
                tokn = new Token(finalToken);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())){
                        LPFrontEnd.servletReturnResponseError(req, resp, 
                                LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, LPFrontEnd.setLanguage(req));              
                        return null;                             
                }
            }
            
            Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, tokn, actionNm);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(req, resp, actionEnabled);
                return null;                             
            }            
            actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, tokn.getUserRole(), actionNm);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
                LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(req, null, actionEnabled);
                return null;                             
            }                        
        }
*/
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, false, null);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, String theActionName){

        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, true, false, theActionName);
        }
        return theSession;
    }

}
