/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_DOCUMENTATION;
import static com.labplanet.servicios.moduleinspectionlotrm.InspLotRMAPI.MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE;
import databases.Rdbms;
import databases.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.testingscripts.TestingAuditIds;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import functionaljavaa.testingscripts.TestingMessageCodeVisited;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.globalvariables.GlobalVariables;
import static trazit.session.ProcReqSessionAutomatisms.markAsExpiredTheExpiredObjects;

/**
 *
 * @author User
 */
public class ProcedureRequestSession {
    
    private static ProcedureRequestSession theSession;
    private String procedureInstance;
    private String actionName;
    private String dbName;
    private Token token;
    private Token previousToken;
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
    private ResponseMessages rspMessages;
    private BusinessRules busRulesProcInstance;
    private BusinessRules busRulesTesting;
    private SessionAuditActions sessionAuditActions;
    
    private ProcedureRequestSession(HttpServletRequest request, HttpServletResponse response, Boolean isForTesting, Boolean isForUAT, Boolean isQuery, String theActionName, Boolean isPlatform, Boolean isForDocumentation){
        try{
        if (request==null) return;
        this.language = LPFrontEnd.setLanguage(request); 
        this.isForTesting=isForTesting;
        this.sessionAuditActions=new SessionAuditActions();
        String finalToken = "";
        Token tokn = null;
        String dbName = "";
        if (!isForDocumentation){
            dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);            
            finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);         
            if (finalToken==null || finalToken.length()==0){
                this.hasErrors=true;
                this.errorMessage="No token provided";
                return;            
            }
            tokn = new Token(finalToken);
            if ( (!LPNulls.replaceNull(dbName).equalsIgnoreCase(LPNulls.replaceNull(tokn.getDbName()))) ){
                this.hasErrors=true;
                this.errorMessage="This dbName does not match the one in the token.";
                return;            
            }
            this.dbName=dbName;
        }
        Object[] areMandatoryParamsInResponse = null;        
        if (!isForTesting){
            if (isPlatform)
                areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
            else if (isForDocumentation)
                areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET_DOCUMENTATION.split("\\|"));                                       
            else
                areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET_PROCEDURE.split("\\|"));                       
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
                this.hasErrors=true;
                this.errorMessage=LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getName()+areMandatoryParamsInResponse[1].toString();                
                return;          
            }                 
            String actionNm = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
            if (actionNm==null || actionNm.length()==0)
                actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);        
            this.actionName=actionNm;
        }else
            this.actionName=theActionName;
        String procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);     
        if (dbName==null || dbName.length()==0)        
            Rdbms.stablishDBConection();
        else
            Rdbms.stablishDBConection(dbName);
        if (!LPFrontEnd.servletStablishDBConection(request, response)){
            this.hasErrors=true;
            this.errorMessage="db connection not stablished";
            return;
        }           
        if (!isPlatform)
            this.busRulesProcInstance= new BusinessRules(procInstanceName, null);        
        
        if (!isForUAT && !isForDocumentation){
            finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);   
            if (finalToken!=null){
                tokn = new Token(finalToken);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())){
                        this.hasErrors=true;
                        this.errorMessage=LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName();
                        return;                             
                }
                this.token=tokn;
                this.previousToken=tokn;
                this.tokenStr=finalToken;
            }
            this.procedureInstance=procInstanceName;
        }
        if (!isForTesting && !isForUAT && !isQuery && !isPlatform && !isForDocumentation){ 
            Object[] theProcActionEnabled = isTheProcActionEnabled(tokn, procInstanceName, actionName, this.busRulesProcInstance);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(theProcActionEnabled[0].toString())){
                this.hasErrors=true;
                this.errorMessage=theProcActionEnabled[theProcActionEnabled.length-1].toString();
                return ;                           
            }            
            
        }
        if (!isForTesting && !isForUAT && !isQuery && !isPlatform && !isForDocumentation){  
            Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, token, actionName, this.busRulesProcInstance);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                return ;                           
            }            
            actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName, this.busRulesProcInstance);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())){            
                this.hasErrors=true;
                this.errorMessage=actionEnabled[actionEnabled.length-1].toString();
                return ;                           
            }                        
        }
        if (!isForTesting && !isForUAT && !isQuery && !isForDocumentation){            
            this.auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(request, null, language);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())){
                this.hasErrors=true;
                this.errorMessage=this.auditAndUsrValid.getCheckUserValidationPassesDiag()[this.auditAndUsrValid.getCheckUserValidationPassesDiag().length-1].toString();
                return;          
            }     
            String schemaConfigName=null;
            if (isPlatform)
                schemaConfigName=GlobalVariables.Schemas.CONFIG.getName();
            else
                schemaConfigName=LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
            Rdbms.setTransactionId(schemaConfigName);
        }
        if (isForTesting){
            this.tstAuditObj = TestingAuditIds.getInstance();
            this.busRuleVisited = TestingBusinessRulesVisited.getInstance();
            this.msgCodeVisited = TestingMessageCodeVisited.getInstance();
        }
        this.isForQuery=isForQuery;
        this.hasErrors=false;
        if (this.tokenStr==null)
            this.tokenStr=finalToken;
        rspMessages=ResponseMessages.getInstance();
        markAsExpiredTheExpiredObjects(this.procedureInstance);
        }catch(Exception e){
            this.hasErrors=true;
            if (this.rspMessages==null)
                this.errorMessage=e.getMessage();
            else{
                Object[][] mainMessage = this.rspMessages.getMainMessage();
                this.errorMessage=mainMessage[0][0].toString();
                //messages.addMainForError("db error", new Object[]{ex.getMessage()});
            }
            
        }
    }
    
    public void killIt(){
       // if (1==1) return;
//        if (!this.isForQuery) 
            this.theSession=null;
        if (this.isForQuery!=null && !this.isForQuery){
            this.token=null;
            this.previousToken=null;
        }
        this.actionName=null;
        this.dbName=null;
        this.isForTesting=null;
        this.procedureInstance=null;
        if (tstAuditObj!=null) tstAuditObj.killIt();
        if (busRuleVisited!=null) busRuleVisited.killIt();
        if (msgCodeVisited!=null) msgCodeVisited.killIt();
        if (rspMessages!=null) rspMessages.killInstance();
        if (this.auditAndUsrValid!=null) this.auditAndUsrValid.killInstance();
        if (this.busRulesProcInstance!=null) this.busRulesProcInstance=null;
        if (this.busRulesTesting!=null) this.busRulesTesting=null;
        if (this.sessionAuditActions!=null) this.sessionAuditActions=null;
        Rdbms.closeRdbms(); 
    }
    
    public String getActionName(){
        return this.actionName;
    }
    public String getDbName(){
        return this.dbName;
    }
    public String getLanguage(){
        return this.language;
    }
    public Token getToken(){
        return this.token;
    }
    public Token getPreviousToken(){
        return this.previousToken;
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
    public ResponseMessages getMessages(){
        if (this.rspMessages==null)return ResponseMessages.getInstance();
        return this.rspMessages;
    }
    public AuditAndUserValidation getAuditAndUsrValid(){
        return this.auditAndUsrValid;
    }
    public SessionAuditActions getAuditActions(){
        return this.sessionAuditActions;
    }
    public void addAuditAction(Integer audId, String auditAction){        
        this.sessionAuditActions.addAuditAction(audId, auditAction);        
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
    public BusinessRules getBusinessRulesProcInstance(){
        return this.busRulesProcInstance;
    }
    public BusinessRules getBusinessRulesTesting(){
        return this.busRulesTesting;
    }
   
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        return getInstanceForQueries(req, resp, isTesting, false);
    }
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, true, null, isPlatform, false);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForDocumentation(HttpServletRequest req, HttpServletResponse resp){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, false, false, true, null, false, true);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        return getInstanceForActions(req, resp, isTesting, false);
    }
    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, false, false, null, isPlatform, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, String theActionName){

        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, isTesting, true, false, theActionName, false, false);
        }
        return theSession;
    }
    public static Object[]  isTheProcActionEnabled(Token tokn, String procInstanceName, String actionNm, BusinessRules procBusinessRules){
        Boolean passCheckers=true;
//        String actionNm = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
//        String procInstanceName = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);            
//        String finalToken = req.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
//        Token tokn = null;
//        if (finalToken!=null){
//            tokn = new Token(finalToken);
            if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())) 
                return LPPlatform.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null);
/*            {
                    LPFrontEnd.servletReturnResponseError(req, resp, 
                            LPPlatform.ApiErrorTraping.INVALID_TOKEN.getName(), null, LPFrontEnd.setLanguage(req));              
                    return null;                             
            }*/
//        }

        Object[] actionEnabled = LPPlatform.procActionEnabled(procInstanceName, tokn, actionNm, procBusinessRules);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())) return actionEnabled;
/*        {
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(req, resp, actionEnabled);
            return null;                             
        }            */
        actionEnabled = LPPlatform.procUserRoleActionEnabled(procInstanceName, tokn.getUserRole(), actionNm, procBusinessRules);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled[0].toString())) return actionEnabled;
/*        {            
            LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(req, null, actionEnabled);
            return null;                             
        }                        */
        return LPPlatform.trapMessage(LPPlatform.LAB_TRUE, "allFine", null);
    }
    public void setBusinessRulesTesting(BusinessRules br){
        this.busRulesTesting=br;
        return;
    }
    public void setAlternativeToken(Token newToken){
        this.previousToken=new Token(this.tokenStr);
        this.token=newToken;
    }
    public void setMainToken(){        
        this.token=new Token(this.tokenStr);
        this.previousToken=new Token(this.tokenStr);
    }

}
