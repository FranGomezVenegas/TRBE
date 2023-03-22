/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package platform.app.apis;



import com.labplanet.servicios.app.GlobalAPIsParams;
import static com.labplanet.servicios.app.GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS;
import static com.labplanet.servicios.app.InvestigationAPI.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.Rdbms;
import databases.SqlStatement;
import databases.SqlStatementEnums;
import databases.SqlWhere;
import databases.TblsApp;
import databases.TblsDataAudit;
import databases.features.Token;
import static functionaljavaa.audit.AuditUtilities.getUserSessionProceduresList;
import static functionaljavaa.audit.AuditUtilities.userSessionExistAtProcLevel;
import static functionaljavaa.testingscripts.LPTestingOutFormat.getAttributeValue;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPAPIArguments;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPNulls;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntEndpoints;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.enums.EnumIntTables;
import trazit.globalvariables.GlobalVariables;
import trazit.globalvariables.GlobalVariables.ApiUrls;
import trazit.queries.QueryUtilitiesEnums;


/**
 *
 * @author User
 */
public class UserSessionAPIqueries extends HttpServlet {
    public enum UserSessionAPIqueriesEndpoints implements EnumIntEndpoints{
        /**
         *
         */
        USER_SESSIONS("USER_SESSIONS", "",new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_SESSION_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),
            new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_PERSON, LPAPIArguments.ArgumentType.STRING.toString(), true, 7),
            new LPAPIArguments(TblsApp.AppSession.DATE_STARTED.getName().toLowerCase(), LPAPIArguments.ArgumentType.DATE.toString(), false, 8),
            new LPAPIArguments(REQUEST_PARAM_NUM_DAYS, LPAPIArguments.ArgumentType.INTEGER.toString(), false, 9)            
            },
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsApp.TablesApp.APP_SESSION.getTableName()).build()).build() ),
        USER_SESSION_INCLUDING_AUDIT_HISTORY("USER_SESSION_INCLUDING_AUDIT_HISTORY", "",new LPAPIArguments[]{new LPAPIArguments(GlobalAPIsParams.REQUEST_PARAM_USER_SESSION_ID, LPAPIArguments.ArgumentType.INTEGER.toString(), true, 6),},
            Json.createArrayBuilder().add(Json.createObjectBuilder().add(GlobalAPIsParams.LBL_REPOSITORY, GlobalVariables.Schemas.DATA.getName())
                .add("table", TblsApp.TablesApp.APP_SESSION.getTableName()).build()).build() ),
        ;
        private UserSessionAPIqueriesEndpoints(String name, String successMessageCode, LPAPIArguments[] argums, JsonArray outputObjectTypes){
            this.name=name;
            this.successMessageCode=successMessageCode;
            this.arguments=argums;  
            this.outputObjectTypes=outputObjectTypes;            
        } 
        public  HashMap<HttpServletRequest, Object[]> testingSetAttributesAndBuildArgsArray(HttpServletRequest request, Object[][] contentLine, Integer lineIndex){  
            HashMap<HttpServletRequest, Object[]> hm = new HashMap();
            Object[] argValues=new Object[0];
            for (LPAPIArguments curArg: this.arguments){                
                argValues=LPArray.addValueToArray1D(argValues, curArg.getName()+":"+getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
                request.setAttribute(curArg.getName(), getAttributeValue(contentLine[lineIndex][curArg.getTestingArgPosic()], contentLine));
            }  
            hm.put(request, argValues);            
            return hm;
        }        
        @Override public String getName(){            return this.name;        }
        @Override public String getSuccessMessageCode(){            return this.successMessageCode;        }           
        @Override public JsonArray getOutputObjectTypes() {return outputObjectTypes;}     
        @Override public LPAPIArguments[] getArguments() {            return arguments;        } 
        @Override        public String getApiUrl(){return ApiUrls.APP_USER_SESSIONS_QUERIES.getUrl();}
        private final String name;
        private final String successMessageCode;  
        private final LPAPIArguments[] arguments;
        private final JsonArray outputObjectTypes;        
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)            throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
                LPFrontEnd.servletReturnResponseError(request, response, 
                        LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
                return;                             
        }
        UserSessionAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = UserSessionAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
    try (PrintWriter out = response.getWriter()) {       
        Object[] argValues=LPAPIArguments.buildAPIArgsumentsArgsValues(request, endPoint.getArguments());   
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}          

        switch (endPoint){
            case USER_SESSIONS:             
                String[] fieldsToRetrieve=getAllFieldNames(TblsApp.TablesApp.APP_SESSION.getTableFields());                
                String[] whereFldName = new String[]{};
                Object[] whereFldValue = new Object[]{};
                LPAPIArguments[] apiArgs=endPoint.getArguments();
                int iVal=0;
                for (Object curValue: argValues){
                    if (curValue!=null){
                        whereFldName=LPArray.addValueToArray1D(whereFldName, apiArgs[iVal].getName());
                        whereFldValue=LPArray.addValueToArray1D(whereFldValue, curValue);
                    }
                    iVal++;
                }
                SqlWhere sW=new SqlWhere();
                sW.addConstraint(TblsApp.AppSession.PERSON, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{token.getPersonName()}, null);

                String samplingDayStart = request.getParameter(TblsApp.AppSession.DATE_STARTED.getName().toLowerCase()+"_start");
                String samplingDayEnd = request.getParameter(TblsApp.AppSession.DATE_STARTED.getName().toLowerCase()+"_end");
                Object[] buildDateRangeFromStrings = databases.SqlStatement.buildDateRangeFromStrings(TblsApp.AppSession.DATE_STARTED.getName().toLowerCase(), samplingDayStart, samplingDayEnd);
                if (LPPlatform.LAB_TRUE.equalsIgnoreCase(buildDateRangeFromStrings[0].toString())){                    
                    if (buildDateRangeFromStrings.length>3)
                        sW.addConstraint(TblsApp.AppSession.DATE_STARTED, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{buildDateRangeFromStrings[2], buildDateRangeFromStrings[3]}, null);
//                        whereFldValue=LPArray.addValueToArray1D(whereFldValue, buildDateRangeFromStrings[3]);
                    else
                        sW.addConstraint(TblsApp.AppSession.DATE_STARTED, SqlStatement.WHERECLAUSE_TYPES.EQUAL, new Object[]{buildDateRangeFromStrings[2]}, null);
                }
                String numDays = LPNulls.replaceNull(request.getParameter(GlobalAPIsParams.REQUEST_PARAM_NUM_DAYS.toLowerCase()));
                if (LPNulls.replaceNull(samplingDayStart).length()==0&&numDays.length()==0)
                    numDays ="7";
                if (numDays.length()>0){
                    int numDaysInt=0-Integer.valueOf(numDays);                   
                    Date refDate=LPNulls.replaceNull(samplingDayStart).length()==0 ? LPDate.getCurrentDateWithNoTime(): (Date) buildDateRangeFromStrings[2];
                    Date refAgoDays = LPDate.addDays(refDate, numDaysInt);
                    sW.addConstraint(TblsApp.AppSession.DATE_STARTED, SqlStatement.WHERECLAUSE_TYPES.BETWEEN, new Object[]{refAgoDays, refDate}, null);
                }
                Object[][] userSessionInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.APP_SESSION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.APP_SESSION, "ALL"),
                    sW, new String[]{TblsApp.AppSession.SESSION_ID.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                JSONArray userSessionArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(userSessionInfo[0][0].toString())){
                    for (Object[] currUsrSession: userSessionInfo){
                        JSONObject userSessionObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currUsrSession);
                        userSessionArr.add(userSessionObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, userSessionArr);
                return; 
            case USER_SESSION_INCLUDING_AUDIT_HISTORY:
                fieldsToRetrieve=getAllFieldNames(TblsApp.TablesApp.APP_SESSION.getTableFields());
                userSessionInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.APP_SESSION, 
                    EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.APP_SESSION, "ALL"),
                    new String[]{TblsApp.AppSession.SESSION_ID.getName()}, new Object[]{argValues[0]},
                    new String[]{TblsApp.AppSession.SESSION_ID.getName()+SqlStatementEnums.SORT_DIRECTION.DESC.getSqlClause()});
                userSessionArr = new JSONArray();
                if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(userSessionInfo[0][0].toString())){
                    JSONArray procAuditArr = new JSONArray();
                    for (Object[] currUsrSession: userSessionInfo){
                        Integer sessionId=-1;
                        if (LPArray.valueInArray(fieldsToRetrieve, TblsApp.AppSession.SESSION_ID.getName()))
                            sessionId=(Integer) currUsrSession[LPArray.valuePosicInArray(fieldsToRetrieve, TblsApp.AppSession.SESSION_ID.getName())];
                        JSONObject userSessionObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieve, currUsrSession);
                        String[] userSessionProceduresList = getUserSessionProceduresList(fieldsToRetrieve, currUsrSession);
                        for (String curProc: userSessionProceduresList){
                            JSONObject procAuditJson = new JSONObject();
                            procAuditJson.put("procedure", curProc);
                            if (!userSessionExistAtProcLevel(curProc, sessionId)){
                                procAuditJson.put("proc_audit_records", "No actions performed during this session on this procedure");
                            }else{
                                try{
                                    for (EnumIntTables curTable: TblsDataAudit.TablesDataAudit.values()){                                        
                                        String[] procAuditTablesFieldsToRetrieve=getAllFieldNames(curTable.getTableFields());
                                        Object[][] dataAuditCurTableInfo=QueryUtilitiesEnums.getTableData(curTable,
                                            EnumIntTableFields.getTableFieldsFromString(curTable, "ALL"),
                                            new String[]{TblsDataAudit.Sample.APP_SESSION_ID.getName()}, new Object[]{sessionId},                                             
                                            new String[]{TblsDataAudit.Sample.AUDIT_ID.getName()}, curProc);
                                        if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(dataAuditCurTableInfo[0][0].toString())){
                                            JSONArray procAuditCurTableArr = new JSONArray();
                                            JSONArray auditCurTableArr = new JSONArray();
                                            JSONObject procAuditCurTableJson = new JSONObject();
                                            for (Object[] curTblAuditRec: dataAuditCurTableInfo){  
                                                JSONObject procAuditTablesJson=LPJson.convertArrayRowToJSONObject(procAuditTablesFieldsToRetrieve, curTblAuditRec);
                                                auditCurTableArr.add(procAuditTablesJson);
                                            }
                                            procAuditCurTableJson.put("audit_records", auditCurTableArr);
                                            procAuditCurTableJson.put("table", curTable);
                                            procAuditCurTableArr.add(procAuditCurTableJson);
                                            procAuditJson.put("proc_audit_records", procAuditCurTableArr);
                                        }
                                    }
                                }catch(Exception e){
                                    procAuditJson.put("proc_audit_records", "error: "+e.getMessage());
                                }    
                            }
                            procAuditArr.add(procAuditJson);
                        }
                        userSessionObj.put("audit_actions", procAuditArr);
                        userSessionArr.add(userSessionObj);
                    }
                }
                Rdbms.closeRdbms();  
                LPFrontEnd.servletReturnSuccess(request, response, userSessionArr);
/*            case USER_SESSION_AUDIT_HISTORY:
                String statusClosed=Parameter.getBusinessRuleProcedureFile(procInstanceName, DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getAreaName(), DataProgramCorrectiveActionBusinessRules.STATUS_CLOSED.getTagName());
                JSONArray jArray = new JSONArray(); 
                if (!isProgramCorrectiveActionEnable(procInstanceName)){
                  JSONObject jObj=new JSONObject();
                  jArray.add(jObj.put(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), "program corrective action not active!"));
                }
                else{
                  Object[][] investigationResultsPendingDecision = Rdbms.getRecordFieldsByFilter(LPPlatform.buildSchemaName(procInstanceName, GlobalVariables.Schemas.PROCEDURE.getName()), TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableName(), 
                          new String[]{TblsProcedure.ProgramCorrectiveAction.STATUS.getName()+"<>"}, 
                          new String[]{statusClosed}, 
                          getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()), new String[]{TblsProcedure.ProgramCorrectiveAction.PROGRAM_NAME.getName()});
                  if (LPPlatform.LAB_FALSE.equalsIgnoreCase(investigationResultsPendingDecision[0][0].toString()))LPFrontEnd.servletReturnSuccess(request, response, new JSONArray());


                  for (Object[] curRow: investigationResultsPendingDecision){
                    JSONObject jObj=LPJson.convertArrayRowToJSONObject(getAllFieldNames(TblsProcedure.TablesProcedure.PROGRAM_CORRECTIVE_ACTION.getTableFields()), curRow);
                    jArray.add(jObj);
                  }
                }
                                    
                LPFrontEnd.servletReturnSuccess(request, response, jArray);
                break;                */
        default: 
        }
    }catch(Exception e){      
        Rdbms.closeRdbms();  
        String[] errObject = new String[]{e.getMessage()};
        Object[] errMsg = LPFrontEnd.responseError(errObject, language, null);
        LPFrontEnd.servletReturnResponseErrorLPFalseDiagnostic(request, response, errMsg);
    } finally {
        // release database resources
       Rdbms.closeRdbms();  
        try {
                                
            Rdbms.closeRdbms();  
        } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }           
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */  
}
