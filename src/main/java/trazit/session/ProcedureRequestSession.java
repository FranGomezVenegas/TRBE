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
import static databases.features.DbEncryption.getEncryptFields;
import databases.Rdbms;
import databases.features.Token;
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
import lbplanet.utilities.LPPlatform.LpPlatformSuccess;
import trazit.enums.EnumIntEndpoints;
import trazit.globalvariables.GlobalVariables;
import static trazit.session.ProcReqSessionAutomatisms.markAsExpiredTheExpiredObjects;

/**
 *
 * @author User
 */
public class ProcedureRequestSession {

    /**
     * @return the isTransactional
     */
    public Boolean getIsTransactional() {
        return isTransactional;
    }
    
    private static ProcedureRequestSession theSession;
    private String procedureInstance;
    private Integer procedureVersion;
    private String procedureHashCode;
    private String actionName;
    private String dbName;
    private Token token;
    private Token previousToken;
    private String tokenStr;
    private String language;
    private Boolean isForTesting;
    private Boolean isForQuery;
    private Boolean isQuery;
    private Boolean isPlatform;
    private Boolean isForDocumentation;
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
    private EnumIntEndpoints actionEndpoint;    
    private Boolean newProcedureHashCodeGenerated;
    private Object[] appEncryptFields;
    private Object[] procedureEncryptFields;
    private Boolean isTransactional;
    private DbLogSummary dbLogSummary;
    
    
    private ProcedureRequestSession(HttpServletRequest request, HttpServletResponse response, EnumIntEndpoints actionEndpoint, Boolean isForTesting, Boolean isForUAT, Boolean isQuery, String theActionName, Boolean isPlatform, Boolean isForDocumentation){
        try{
        if (request==null) return;
        this.isTransactional=Rdbms.transactionMode;
        this.dbLogSummary=new DbLogSummary();
        this.newProcedureHashCodeGenerated=false;
        this.isQuery=isQuery;
        this.isPlatform=isPlatform;
        this.isForDocumentation=isForDocumentation;
        if (actionEndpoint!=null) this.actionEndpoint=actionEndpoint;
        busRuleVisited=new TestingBusinessRulesVisited();
        this.language = LPFrontEnd.setLanguage(request); 
        this.isForTesting=isForTesting;
        String paramIsTesting= request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING);            
        if (paramIsTesting!=null && Boolean.valueOf(paramIsTesting))
            this.isForTesting=true;
        this.sessionAuditActions=new SessionAuditActions();
        
        String finalToken = "";
        Token tokn = null;
        String dbName = "";
        if (!isForDocumentation){
            dbName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);            
            finalToken = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN); 
            if (finalToken==null || finalToken.length()==0)
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
                this.errorMessage=LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode()+areMandatoryParamsInResponse[1].toString();                
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
        this.appEncryptFields=getEncryptFields(dbName, true, null);
        if (!isPlatform)
            this.procedureEncryptFields=getEncryptFields(dbName, false, procInstanceName);
        if (!isPlatform)
            this.busRulesProcInstance= new BusinessRules(procInstanceName, null);        
        
        if (!isForUAT && !isForDocumentation){
            finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);   
            if (finalToken!=null){
                tokn = new Token(finalToken);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())){
                        this.hasErrors=true;
                        this.errorMessage=LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode();
                        return;                             
                }
                this.token=tokn;
                this.previousToken=tokn;
                this.tokenStr=finalToken;
            }
            this.procedureInstance=procInstanceName;
        }
        if (this.token!=null && !isPlatform){            
            this.procedureVersion=this.token.getProcedureInstanceVersion(procInstanceName);
            this.procedureHashCode=this.token.getProcedureInstanceHashCode(procInstanceName);
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
            this.auditAndUsrValid=AuditAndUserValidation.getInstanceForActions(request, null, language, this.busRulesProcInstance, this.isPlatform);
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
        //this.isForQuery=isForQuery;
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
//        LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
       // if (1==1) return;
//        if (!this.isForQuery) 
            Rdbms.closeTransaction(); 
            this.theSession=null;
/*        if (this.getDbLogSummary()!=null){
            Boolean hasAlters=this.getDbLogSummary().hasDbAlterActions();
            if (hasAlters){
                Integer numIns=this.getDbLogSummary().getNumInserts();
            }
        }*/
        
        if (this.getIsForQuery()!=null && !this.getIsForQuery()){
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
    public void auditActionsKill(){
        if (this.sessionAuditActions!=null) this.sessionAuditActions=new SessionAuditActions();
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
    public Integer getProcedureInstanceVersion(){
        return this.procedureVersion;
    }
    public String getProcedureHashCode(){
        return this.procedureHashCode;
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
    public void addAuditAction(Integer audId, String auditAction, String auditPrettyEn, String auditPrettyEs){
        this.sessionAuditActions.addAuditAction(audId, auditAction, auditPrettyEn, auditPrettyEs);        
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
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting){
        return getInstanceForQueries(req, resp, isTesting, false);
    }
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, null, isTesting, false, true, null, isPlatform, false);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, endPoint, isTesting, false, true, null, isPlatform, false);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForDocumentation(HttpServletRequest req, HttpServletResponse resp){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, null, false, false, true, null, false, true);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForDocumentation(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, null, false, false, true, null, false, true);
        }            
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting){
        return getInstanceForActions(req, resp, null, isTesting, false);
    }
    public static ProcedureRequestSession getInstanceForActionsWithEndpoint(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting){
        return getInstanceForActions(req, resp, endPoint, isTesting, false);
    }
    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, null, isTesting, false, false, null, isPlatform, false);
        }
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, Boolean isPlatform){
        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, endPoint, isTesting, false, false, null, isPlatform, false);
        }
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, String theActionName){

        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, null, isTesting, true, false, theActionName, false, false);
        }
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, String theActionName){

        if (theSession==null || theSession.getTokenString()==null){
            theSession=new ProcedureRequestSession(req, resp, endPoint, isTesting, true, false, theActionName, false, false);
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
                return ApiMessageReturn.trapMessage(LPPlatform.LAB_FALSE, LPPlatform.ApiErrorTraping.INVALID_TOKEN, null);
/*            {
                    LPFrontEnd.servletReturnResponseError(req, resp, 
                            LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, LPFrontEnd.setLanguage(req));              
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

return ApiMessageReturn.trapMessage(LPPlatform.LAB_TRUE, LpPlatformSuccess.ALL_FINE, null);
    }
    public void setBusinessRulesTesting(BusinessRules br){
        this.busRulesTesting=br;
    }
    public void setBusinessProcInstance(BusinessRules br, String procInstanceName, String finalToken){
        if (theSession!=null || theSession.getTokenString()!=null){
            theSession.busRulesProcInstance=br;
            theSession.procedureInstance=procInstanceName;
            theSession.tokenStr=finalToken;
        }
    }
    public void setActionNameForTesting(Integer scriptId, Integer stepId, String actionName){
        String tstAction="Script:"+scriptId.toString()+" Step:"+stepId.toString()+" ActionName:"+actionName;
        this.actionName=tstAction;
    }
    public void setAlternativeToken(Token newToken){
        this.previousToken=new Token(this.tokenStr);
        this.token=newToken;
    }
    public void setMainToken(){        
        this.token=new Token(this.tokenStr);
        this.previousToken=new Token(this.tokenStr);
    }
    public void setNewProcedureHashCode(String newHashCode){
        this.procedureHashCode=newHashCode;
        this.newProcedureHashCodeGenerated=true;
    }
    /**
     * @return the actionEndpoint
     */
    public EnumIntEndpoints getActionEndpoint() {
        return actionEndpoint;
    }

    /**
     * @return the newProcedureHashCodeGenerated
     */
    public Boolean getNewProcedureHashCodeGenerated() {
        return newProcedureHashCodeGenerated;
    }

    /**
     * @return the isForQuery
     */
    public Boolean getIsForQuery() {
        return isForQuery;
    }

    /**
     * @return the isQuery
     */
    public Boolean getIsQuery() {
        return isQuery;
    }

    /**
     * @return the isPlatform
     */
    public Boolean getIsPlatform() {
        return isPlatform;
    }

    /**
     * @return the isForDocumentation
     */
    public Boolean getIsForDocumentation() {
        return isForDocumentation;
    }

    /**
     * @return the appEncryptFields
     */
    public Object[] getAppEncryptFields() {
        return appEncryptFields;
    }

    /**
     * @return the procedureEncryptFields
     */
    public Object[] getProcedureEncryptFields() {
        return procedureEncryptFields;
    }

    /**
     * @return the dbLogSummary
     */
    public DbLogSummary getDbLogSummary() {
        if (dbLogSummary==null)
            this.dbLogSummary=new DbLogSummary();
        return dbLogSummary;
    }

}
