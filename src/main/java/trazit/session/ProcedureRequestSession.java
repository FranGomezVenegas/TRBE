/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trazit.session;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static databases.features.DbEncryption.getEncryptFields;
import databases.Rdbms;
import databases.features.Token;
import functionaljavaa.audit.AuditAndUserValidation;
import functionaljavaa.businessrules.ActionsControl;
import functionaljavaa.businessrules.BusinessRules;
import functionaljavaa.testingscripts.TestingAuditIds;
import functionaljavaa.testingscripts.TestingBusinessRulesVisited;
import functionaljavaa.testingscripts.TestingMainInfo;
import functionaljavaa.testingscripts.TestingMessageCodeVisited;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntMessages;
import trazit.globalvariables.GlobalVariables;

/**
 *
 * @author User
 */
public class ProcedureRequestSession {

    public static final String MANDATPRMS_MAIN_SERVLET_QUERIES = GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
    public static final String MANDATPRMS_MAIN_SRVLT_DOCUMENTATION = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN;
    public static final String MANDATPRMS_MAIN_SERVLET_PROCEDURE = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;
    public static final String MANDATORY_PARAMS_MAIN_SERVLET = GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME + "|" + GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN + "|" + GlobalAPIsParams.REQUEST_PARAM_DB_NAME;

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
    private Boolean isForProcManagement;
    private Boolean hasErrors;
    private String errorMessage;
    private Object[] errorMessageVariables;
    private AuditAndUserValidation auditAndUsrValid;
    private TestingAuditIds tstAuditObj;
    private TestingBusinessRulesVisited busRuleVisited;
    private TestingMessageCodeVisited msgCodeVisited;
    private String testingOutputFormat;
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
    private TestingMainInfo testingMainInfo;
    private EnumIntMessages errorMessageCodeObj;

    private ProcedureRequestSession(HttpServletRequest request, HttpServletResponse response, EnumIntEndpoints actionEndpoint, Boolean isForTesting, Boolean isForUAT, Boolean isQuery, String theActionName, Boolean isPlatform, Boolean isForDocumentation, Boolean isForProcManagement) {
        try {
            if (request == null) {
                return;
            }
            this.isTransactional = Rdbms.TRANSACTION_MODE;
            this.dbLogSummary = new DbLogSummary();
            this.newProcedureHashCodeGenerated = false;
            this.isQuery = isQuery;
            this.isPlatform = isPlatform;
            this.isForDocumentation = isForDocumentation;
            this.isForProcManagement=isForProcManagement;
            if (actionEndpoint != null) {
                this.actionEndpoint = actionEndpoint;
            }
            busRuleVisited = new TestingBusinessRulesVisited();
            this.language = LPFrontEnd.setLanguage(request);
            this.isForTesting = isForTesting;
            String paramIsTesting = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_IS_TESTING);
            if (paramIsTesting != null && Boolean.valueOf(paramIsTesting)) {
                this.isForTesting = true;
            }
            // By now anything that is for Platform has no isForTesting mode.
            if (isPlatform||isForDocumentation||isForProcManagement){
                this.isForTesting=false;
            }
            if (Boolean.TRUE.equals(this.isForTesting)) {
                this.testingOutputFormat = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_TESTING_OUTPUT_FORMAT);
                if (LPNulls.replaceNull(this.testingOutputFormat).length() == 0) {
                    this.testingOutputFormat = "JSON";
                }
            }
            this.sessionAuditActions = new SessionAuditActions();

            String finalToken = "";
            Token tokn = null;
            String dbNameProp = "";

            dbNameProp = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);
            if (dbNameProp == null) {
                dbNameProp = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_DB_NAME);
            }
            finalToken = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            if (finalToken == null || finalToken.length() == 0) {
                finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
            }
            if (finalToken == null || finalToken.length() == 0) {
                this.hasErrors = true;
                this.errorMessage = "No token provided";
                return;
            }
            tokn = new Token(finalToken);
            if (tokn!=null)this.token=tokn;
            if (Boolean.FALSE.equals(isForDocumentation)) {
                if ((Boolean.FALSE.equals(LPNulls.replaceNull(dbNameProp).equalsIgnoreCase(LPNulls.replaceNull(tokn.getDbName()))))) {
                    this.hasErrors = true;
                    this.errorMessage = "This dbName does not match the one in the token.";
                    return;
                }
                this.dbName = dbNameProp;
            }
            Object[] areMandatoryParamsInResponse = null;
            if (Boolean.FALSE.equals(isForTesting)) {
                if (Boolean.TRUE.equals(isPlatform)) {
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));
                } else if (Boolean.TRUE.equals(isForDocumentation)) {
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATPRMS_MAIN_SRVLT_DOCUMENTATION.split("\\|"));
                } else if (Boolean.TRUE.equals(isQuery)) {
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATPRMS_MAIN_SERVLET_QUERIES.split("\\|"));
                } else {
                    areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATPRMS_MAIN_SRVLT_DOCUMENTATION.split("\\|"));
                }
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())) {
                    this.hasErrors = true;
                    this.errorMessage = LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode() + areMandatoryParamsInResponse[1].toString();
                    return;
                }
                if (Boolean.TRUE.equals(isQuery)) {
                    String actionNm = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_VIEW_NAME);
                    if (actionNm == null || actionNm.length() == 0) {
                        actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_VIEW_NAME);
                    }
                    if (actionNm == null || actionNm.length() == 0) {
                        actionNm = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
                        if (actionNm == null || actionNm.length() == 0) {
                            actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
                        }
                    }
                    if (actionNm == null || actionNm.length() == 0) {
                        this.hasErrors = true;
                        this.errorMessage = LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode() + "viewName or actionName";
                        return;
                    } else {
                        this.actionName = actionNm;
                    }
                } else {
                    String actionNm = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
                    if (actionNm == null || actionNm.length() == 0) {
                        actionNm = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
                    }
                    this.actionName = actionNm;
                }
            } else {
                this.actionName = theActionName;
            }
            
            String procInstanceName = (String) request.getAttribute(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);
            if (procInstanceName == null || procInstanceName.length() == 0) {
                procInstanceName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_PROCINSTANCENAME);
            }
            
            if (dbNameProp == null || dbNameProp.length() == 0) {
                Rdbms.stablishDBConection();
            } else {
                Rdbms.stablishDBConection(dbNameProp);
            }
            if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))) {
                this.hasErrors = true;
                this.errorMessage = "db connection not stablished";
                return;
            }
            this.appEncryptFields = getEncryptFields(dbNameProp, true, null);
            if (Boolean.FALSE.equals(isPlatform)) {
                this.procedureEncryptFields = getEncryptFields(dbNameProp, false, procInstanceName);
            }
            if (Boolean.FALSE.equals(isPlatform)&&Boolean.FALSE.equals(isForProcManagement)) {
                this.busRulesProcInstance = new BusinessRules(procInstanceName, null);
            }

            if (Boolean.FALSE.equals(isForUAT) && Boolean.FALSE.equals(isForDocumentation)) {
                finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);
                if (finalToken != null) {
                    tokn = new Token(finalToken);
                    if (LPPlatform.LAB_FALSE.equalsIgnoreCase(tokn.getUserName())) {
                        this.hasErrors = true;
                        this.errorMessage = LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode();
                        return;
                    }
                    this.token = tokn;
                    this.previousToken = tokn;
                    this.tokenStr = finalToken;
                }
                this.procedureInstance = procInstanceName;
            }
            if (Boolean.FALSE.equals(this.isPlatform)&&Boolean.FALSE.equals(this.isForDocumentation)&&(procInstanceName==null||procInstanceName.length()==0||"undefined".equalsIgnoreCase(procInstanceName))){
                if (Boolean.FALSE.equals((this.isForProcManagement&&"NEW_PROCEDURE".equalsIgnoreCase(this.actionName)))){
                    this.hasErrors = true;
                    this.errorMessage = "procInstanceName argument not found and is mandatory";
                    return;                
                }
            }
            if (this.token != null && !isPlatform && !this.isForDocumentation) {
                this.procedureVersion = this.token.getProcedureInstanceVersion(procInstanceName);
                this.procedureHashCode = this.token.getProcedureInstanceHashCode(procInstanceName);
            }
            if (Boolean.FALSE.equals(isForTesting) && Boolean.FALSE.equals(isForUAT) && Boolean.FALSE.equals(isQuery)
                    && Boolean.FALSE.equals(isPlatform) && Boolean.FALSE.equals(isForDocumentation) && Boolean.FALSE.equals(this.isForProcManagement)) {
                InternalMessage theProcActionEnabled = ActionsControl.isTheProcActionEnabled(tokn, procInstanceName, actionName, this.busRulesProcInstance);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(theProcActionEnabled.getDiagnostic())) {
                    this.hasErrors = true;
                    this.errorMessageCodeObj=theProcActionEnabled.getMessageCodeObj();
                    this.errorMessageVariables=theProcActionEnabled.getMessageCodeVariables();
                    this.errorMessage = theProcActionEnabled.getMessageCodeObj().getErrorCode();
                    return;
                }
            } 
            if (Boolean.FALSE.equals(isForTesting) && Boolean.FALSE.equals(isForUAT) && Boolean.FALSE.equals(isQuery)
                    && Boolean.FALSE.equals(isPlatform) && Boolean.FALSE.equals(isForDocumentation)
                    &&Boolean.FALSE.equals(isForProcManagement)) {
                InternalMessage actionEnabled = ActionsControl.procActionEnabled(procInstanceName, token, actionName, this.busRulesProcInstance, false);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
                    this.hasErrors = true;
                    this.errorMessageCodeObj=actionEnabled.getMessageCodeObj();
                    this.errorMessageVariables=actionEnabled.getMessageCodeVariables();
                    this.errorMessage = actionEnabled.getMessageCodeObj().getErrorCode();
                    return;
                }
                actionEnabled = ActionsControl.procUserRoleActionEnabled(procInstanceName, token.getUserRole(), actionName, this.busRulesProcInstance);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(actionEnabled.getDiagnostic())) {
                    this.hasErrors = true;
                    this.errorMessageVariables=actionEnabled.getMessageCodeVariables();
                    this.errorMessage = actionEnabled.getMessageCodeObj().getErrorCode();
                    this.errorMessageCodeObj=actionEnabled.getMessageCodeObj();
                    return;
                }
            }
            if (Boolean.FALSE.equals(isForTesting) && Boolean.FALSE.equals(isForUAT) && Boolean.FALSE.equals(isQuery) && Boolean.FALSE.equals(isForDocumentation)
                    &&Boolean.FALSE.equals(isForProcManagement)) {
                this.auditAndUsrValid = AuditAndUserValidation.getInstanceForActions(request, language, this.busRulesProcInstance, this.isPlatform);
                if (LPPlatform.LAB_FALSE.equalsIgnoreCase(this.auditAndUsrValid.getCheckUserValidationPassesDiag()[0].toString())) {
                    this.hasErrors = true;
                    
                    this.errorMessage = this.auditAndUsrValid.getCheckUserValidationPassesDiag()[this.auditAndUsrValid.getCheckUserValidationPassesDiag().length - 1].toString();
                    return;
                }
                String schemaConfigName = null;
                if (Boolean.TRUE.equals(isPlatform)) {
                    schemaConfigName = GlobalVariables.Schemas.CONFIG.getName();
                } else {
                    schemaConfigName = LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.CONFIG.getName());
                }
                Rdbms.setTransactionId(schemaConfigName);
            }
            if (Boolean.TRUE.equals(isForTesting)) {
                this.tstAuditObj = TestingAuditIds.getInstance();
                this.busRuleVisited = TestingBusinessRulesVisited.getInstance();
                this.msgCodeVisited = TestingMessageCodeVisited.getInstance();
                this.testingMainInfo = new TestingMainInfo();
            }
            this.hasErrors = false;
            if (this.tokenStr == null) {
                this.tokenStr = finalToken;
            }
            rspMessages = ResponseMessages.getInstance();
        } catch (Exception e) {
            this.hasErrors = true;
            if (this.rspMessages == null) {
                this.errorMessage = e.getMessage();
            } else {
                Object[][] mainMessage = this.rspMessages.getMainMessage();
                this.errorMessage = mainMessage[0][0].toString();
            }
        }
    }

    public void killIt() {
        
            SchedProcedures.schedProcesses(theSession.getToken(), theSession.getProcedureInstance());
        
//        LPSession.addProcessSession(Integer.valueOf(token.getAppSessionId()), new String[]{TblsApp.AppSession.DATE_STARTED.getName()});
        // if (1==1) return;
//        if (!this.isForQuery) 
        Rdbms.closeTransaction();
        ProcedureRequestSession.theSession = null;
        /*        if (this.getDbLogSummary()!=null){
            Boolean hasAlters=this.getDbLogSummary().hasDbAlterActions();
            if (hasAlters){
                Integer numIns=this.getDbLogSummary().getNumInserts();
            }
        }*/

        if (this.getIsForQuery() != null && !this.getIsForQuery()) {
            this.token = null;
            this.previousToken = null;
        }
        this.actionName = null;
        this.dbName = null;
        this.isForTesting = null;
        this.procedureInstance = null;
        if (tstAuditObj != null) {
            tstAuditObj.killIt();
        }
        if (busRuleVisited != null) {
            busRuleVisited.killIt();
        }
        if (msgCodeVisited != null) {
            msgCodeVisited.killIt();
        }
        if (rspMessages != null) {
            rspMessages.killInstance();
        }
        if (this.auditAndUsrValid != null) {
            this.auditAndUsrValid.killInstance();
        }
        if (this.busRulesProcInstance != null) {
            this.busRulesProcInstance = null;
        }
        if (this.busRulesTesting != null) {
            this.busRulesTesting = null;
        }
        if (this.sessionAuditActions != null) {
            this.sessionAuditActions = null;
        }
        if (this.testingMainInfo != null) {
            this.testingMainInfo = null;
        }
        Rdbms.closeRdbms();
    }

    public void auditActionsKill() {
        if (this.sessionAuditActions != null) {
            this.sessionAuditActions = new SessionAuditActions();
        }
    }

    public String getActionName() {
        return this.actionName;
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getLanguage() {
        return this.language;
    }

    public Token getToken() {
        return this.token;
    }

    public Token getPreviousToken() {
        return this.previousToken;
    }

    public String getTokenString() {
        return this.tokenStr;
    }

    public String getProcedureInstance() {
        return this.procedureInstance;
    }

    public Integer getProcedureInstanceVersion() {
        return this.procedureVersion;
    }

    public String getProcedureHashCode() {
        return this.procedureHashCode;
    }

    public Boolean getIsForTesting() {
        if (this.isForTesting == null) {
            return false;
        }
        return this.isForTesting;
    }

    public Boolean getHasErrors() {
        if (this.hasErrors == null) {
            return true;
        }
        return this.hasErrors;
    }

    public String getErrorMessage() {
        if (this.errorMessage == null) {
            return "";
        }
        return this.errorMessage;
    }
    public EnumIntMessages getErrorMessageCodeObj() {
        if (this.errorMessage == null) {
            return null;
        }
        return this.errorMessageCodeObj;
    }

     
    public Object[] getErrorMessageVariables() {
        if (this.errorMessage == null) {
            return new Object[]{};
        }
        return this.errorMessageVariables;
    }

    

    public ResponseMessages getMessages() {
        if (this.rspMessages == null) {
            return ResponseMessages.getInstance();
        }
        return this.rspMessages;
    }

    public AuditAndUserValidation getAuditAndUsrValid() {
        return this.auditAndUsrValid;
    }

    public SessionAuditActions getAuditActions() {
        return this.sessionAuditActions;
    }

    public void addAuditAction(Integer audId, String auditAction, String auditPrettyEn, String auditPrettyEs) {
        this.sessionAuditActions.addAuditAction(audId, auditAction, auditPrettyEn, auditPrettyEs);
    }

    public TestingAuditIds getTestingAuditObj() {
        return this.tstAuditObj;
    }

    public TestingBusinessRulesVisited getTestingBusinessRulesVisitedObj() {
        return this.busRuleVisited;
    }

    public TestingMessageCodeVisited getTestingMessageCodeVisitedObj() {
        return this.msgCodeVisited;
    }

    public BusinessRules getBusinessRulesProcInstance() {
        return this.busRulesProcInstance;
    }

    public BusinessRules getBusinessRulesTesting() {
        return this.busRulesTesting;
    }

    public TestingMainInfo getTestingMainInfo() {
        return this.testingMainInfo;
    }

    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting) {
        return getInstanceForQueries(req, resp, isTesting, false);
    }

    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting) {
        return getInstanceForQueries(req, resp, isTesting, false);
    }

    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, isTesting, false, true, null, isPlatform, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForQueries(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, Boolean isPlatform) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, endPoint, isTesting, false, true, null, isPlatform, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForDocumentation(HttpServletRequest req, HttpServletResponse resp) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, false, false, true, null, false, true, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForDocumentation(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, false, false, true, null, false, true, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting) {
        return getInstanceForActions(req, resp, null, isTesting, false);
    }

    public static ProcedureRequestSession getInstanceForActionsWithEndpoint(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting) {
        return getInstanceForActions(req, resp, endPoint, isTesting, false);
    }

    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, Boolean isPlatform) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, isTesting, false, false, null, isPlatform, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForActions(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, Boolean isPlatform) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, endPoint, isTesting, false, false, null, isPlatform, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, Boolean isTesting, String theActionName) {

        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, isTesting, true, false, theActionName, false, false, false);
        }
        return theSession;
    }

    public static ProcedureRequestSession getInstanceForUAT(HttpServletRequest req, HttpServletResponse resp, EnumIntEndpoints endPoint, Boolean isTesting, String theActionName) {

        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, endPoint, isTesting, true, false, theActionName, false, false, false);
        }
        return theSession;
    }
    public static ProcedureRequestSession getInstanceForProcManagement(HttpServletRequest req, HttpServletResponse resp,  Boolean isTesting) {
        if (theSession == null || theSession.getTokenString() == null) {
            theSession = new ProcedureRequestSession(req, resp, null, isTesting, true, false, null, false, false, true);
        }
        return theSession;
    }
    

    public void setBusinessRulesTesting(BusinessRules br) {
        this.busRulesTesting = br;
    }

    public void setBusinessProcInstance(BusinessRules br, String procInstanceName, String finalToken) {
        if (theSession != null || theSession.getTokenString() != null) {
            theSession.busRulesProcInstance = br;
            theSession.procedureInstance = procInstanceName;
            theSession.tokenStr = finalToken;
        }
    }

    public void setActionNameForTesting(Integer scriptId, Integer stepId, String actionName) {
        String tstAction = "Script:" + scriptId.toString() + " Step:" + stepId.toString() + " ActionName:" + actionName;
        this.actionName = tstAction;
    }

    public void setAlternativeToken(Token newToken) {
        this.previousToken = new Token(this.tokenStr);
        this.token = newToken;
    }

    public void setMainToken() {
        this.token = new Token(this.tokenStr);
        this.previousToken = new Token(this.tokenStr);
    }

    public void setProcInstanceName(String newProcInstanceName) {
        this.procedureInstance = newProcInstanceName;
    }

    public void setNewProcedureHashCode(String newHashCode) {
        this.procedureHashCode = newHashCode;
        this.newProcedureHashCodeGenerated = true;
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
    public Boolean getIsForProcManagement() {
        return this.isForProcManagement;
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
        if (dbLogSummary == null) {
            this.dbLogSummary = new DbLogSummary();
        }
        return dbLogSummary;
    }

    /**
     * @return the testingOutputFormat
     */
    public String getTestingOutputFormat() {
        return testingOutputFormat;
    }

}
